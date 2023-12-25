(ns s-exp.pact
  (:refer-clojure :exclude [derive vary-meta meta])
  (:require
   [clojure.spec.alpha :as s]
   [s-exp.pact.impl :as impl]
   [s-exp.pact.inspect :as si]))

(def default-opts
  {:property-key-fn name
   :gen-only-first-and-arg false
   :strict true ; will throw on unknown conversion
   :unknown-spec-default nil})

(defonce registry-ref
  (atom #:s-exp.pact.json-schema{:meta {}
                                 :schema {}
                                 :pred-conformers #{}
                                 :pred-schema {}}))

(defn registry
  "Returns registry"
  []
  @registry-ref)

(defn meta
  "Returns metadata registry or metadata for spec `k`"
  ([] (get (registry) :s-exp.pact.json-schema/meta))
  ([k]
   (get-in (registry) [:s-exp.pact.json-schema/meta k])))

(defn vary-meta
  "Like `clojure.core/vary-meta but on spec `k` metadata"
  [k f & args]
  (swap! registry-ref update-in
         [:s-exp.pact.json-schema/meta k]
         #(apply f % args))
  k)

(defn assoc-meta
  "Assoc `k`->`x` on metadata for `spec` "
  [spec k x]
  (vary-meta spec assoc k x))

(defn with-id
  "Adds $id to spec"
  [spec id]
  (assoc-meta spec :$id id))

(defn with-title
  "Add `title` to spec"
  [k title]
  (assoc-meta k :title title))

(defn with-description
  "Add `description` to spec"
  [k description]
  (assoc-meta k :description description))

(defn with-format
  "Add `format` to spec"
  [k fmt]
  (assoc-meta k :format fmt))

(defn with-pattern
  "Add `pattern` to spec"
  [k p]
  (assoc-meta k :pattern p))

(defn find-id
  "Find first `$id` value in spec hierarchy for spec"
  [k]
  (impl/registry-lookup (meta) k :$id))

(defn find-title
  "Find first `title` value in spec hierarchy for spec"
  [k]
  (impl/registry-lookup (meta) k :title))

(defn find-description
  "Find first `description` value in spec hierarchy for spec"
  [k]
  (impl/registry-lookup (meta) k :description))

(defn find-schema
  "Find first `schema` value in spec hierarchy for spec"
  [k]
  (impl/registry-lookup (meta) k :schema))

(defn find-format
  "Find first `format` value in spec hierarchy for spec"
  [k]
  (impl/registry-lookup (meta) k :format))

(defn find-pattern
  "Find first `pattern` value in spec hierarchy for spec"
  [k]
  (impl/registry-lookup (meta) k :pattern))

(declare schema)

(defn set-pred-conformer!
  "Sets `conformer` and `schema-fn` for predicate parser.
  If a conformer matches, the bindings we get from the s/conform result will be
  passed to `schema-fn` in order to generate an appropriate json-schema value
  for the predicate."
  ([k schema-fn _opts]
   (swap! registry-ref
          (fn [registry-val]
            (-> registry-val
                (update :s-exp.pact.json-schema/pred-conformers conj k)
                (assoc-in [:s-exp.pact.json-schema/pred-schema k] schema-fn)))))
  ([k schema-fn]
   (set-pred-conformer! k schema-fn default-opts)))

(defn json-schema*
  "Like `json-schema`, but doesn't do any caching"
  [k opts]
  (let [ret
        (if-let [schema (find-schema k)]
          schema
          (schema (si/spec-root k) opts))
        ;; FIXME these should re-use a single ancestor call instead of walking
        ;; back the chain again and again
        desc (find-description k)
        fmt (find-format k)
        pattern (find-pattern k)
        id (find-id k)
        title (find-title k)]
    (cond-> ret
      id
      (assoc :$id id)
      title
      (assoc :title title)
      desc
      (assoc :description desc)
      fmt
      (assoc :format fmt)
      pattern
      (assoc :pattern pattern))))

(def hierarchy
  "Internal hierarchy used by `schema`"
  (atom (make-hierarchy)))

(defn derive
  "Like clojure.core/derive but scoped on our `hierarchy`"
  [tag parent]
  (swap! hierarchy
         clojure.core/derive tag parent))

(defmulti schema
  "Dispatches on spec form to generate relevant json-schema for passed form"
  (fn [form _opts]
    (cond
      (set? form) `enum
      (sequential? form) (first form)
      :else form))
  :hierarchy hierarchy)

(defmethod schema `s/coll-of
  [[_ spec & {:as spec-opts :keys [max-count min-count length kind]}]
   opts]
  (let [distinct (or (:distinct spec-opts) (= kind `set?))]
    (cond-> {:type "array"
             :items (json-schema* spec opts)}
      length
      (assoc :minItems length
             :maxItems length)
      max-count
      (assoc :maxItems max-count)
      min-count
      (assoc :minItems min-count)
      distinct
      (assoc :uniqueItems true))))

(defmethod schema `coll?
  [_form _opts]
  {:type "array"})

(derive `list? `coll?)
(derive `vector? `coll?)
(derive `sequential? `coll?)
(derive `s/cat `coll?)

(defmethod schema `s/map-of
  [[_ _ val-spec] opts]
  {:type "object"
   :patternProperties {"*" (json-schema* val-spec opts)}})

(defn- parse-s-keys
  [form]
  (-> (apply hash-map (rest form))
      (update-vals #(->> %
                         flatten
                         ;; hackish, but will do for now
                         (remove #{`or `and})
                         (distinct)))))

(defn- keys->properties [pk {:as opts :keys [property-key-fn]}]
  (let [specs (eduction
               (mapcat (comp val))
               (select-keys pk
                            [:req-un :req :opt :opt-un]))]
    (into {}
          (map (fn [k]
                 [(property-key-fn k) (json-schema* k opts)]))
          specs)))

(defmethod schema `s/keys
  [form {:as opts :keys [property-key-fn]}]
  (let [keys' (parse-s-keys form)
        req-keys (not-empty
                  (select-keys keys'
                               [:req-un :req]))]
    (cond-> {:type "object"
             :properties (keys->properties keys' opts)}
      req-keys
      (assoc :required (into []
                             (comp (mapcat val)
                                   (map property-key-fn))
                             (select-keys keys'
                                          [:req-un :req]))))))

(defmethod schema `nil?
  [_form _opts]
  {:type "null"})

(defmethod schema `s/nilable
  [[_ form] opts]
  {:oneOf [{:type "null"}
           (json-schema* form opts)]})

(defmethod schema `s/multi-spec
  [[_ mm tag-key] opts]
  (let [f (resolve mm)]
    {:oneOf (into []
                  (comp
                   (map (fn extract-spec [[dispatch-val _spec]]
                          (si/spec-root (s/form (f {tag-key dispatch-val})))))
                   (map (fn get-json-schema [k]
                          (json-schema* k opts))))
                  (methods @f))}))

(defmethod schema `enum
  [values _opts]
  {:enum values})

(defmethod schema `string?
  [_form _opts]
  {:type "string"})

(derive `keyword? `string?)

(defmethod schema `uuid?
  [_form _opts]
  {:type "string" :format "uuid"})

(defmethod schema `int?
  [_form _opts]
  {:type "integer" :format "int64"})

(defmethod schema `nat-int?
  [_form _opts]
  {:type "integer"
   :format "int64"
   :minimum 0})

(defmethod schema `pos-int?
  [_form _opts]
  {:type "integer"
   :format "int64"
   :minimum 1})

(defmethod schema `neg-int?
  [_form _opts]
  {:type "integer"
   :format "int64"
   :maximum -1})

(derive `integer? `int?)

(defmethod schema `s/int-in
  [[_ min max] _opts]
  {:type "integer"
   :mininum min
   :maximum (dec max)})

(defmethod schema `number?
  [_form _opts]
  {:type "number"})

(defmethod schema `float?
  [_form _opts]
  {:type "number" :format "float"})

(defmethod schema `double?
  [_form _opts]
  {:type "number" :format "double"})

(defmethod schema `boolean?
  [_form _opts]
  {:type "boolean"})

(defmethod schema `inst?
  [_form _opts]
  {:type "string" :format "date-time"})

(defmethod schema `any?
  [_form _opts]
  {:type "object"})

(derive `map? `any?)

(defmethod schema `s/and
  [[_ & forms] {:as opts :keys [gen-only-first-and-arg]}]
  {:allOf (into []
                (keep #(json-schema* % opts))
                (if gen-only-first-and-arg
                  [(first forms)]
                  forms))})

(defmethod schema `s/merge
  [[_ & forms] opts]
  {:allOf (into []
                (map #(json-schema* % opts))
                forms)})

(defmethod schema `s/or
  [[_ & forms] opts]
  {:oneOf (into []
                (comp
                 (partition-all 2)
                 (map #(json-schema* (second %) opts)))
                forms)})

(derive `s/alt `s/or)

;;

(defn set-pred-schema!
  [spec-key schema-fn]
  (set-pred-conformer! spec-key schema-fn))

(defn- parse-fn
  [form {:as opts}]
  (let [registry-val @registry-ref
        {:s-exp.pact.json-schema/keys [pred-conformers]}
        registry-val]
    (or (impl/pred-conformer registry-val pred-conformers form opts)
        {:type "object" :pact "pred"})))

(defmethod schema `clojure.core/fn
  [form opts]
  (parse-fn form opts))

(defmethod schema :default
  [form opts]
  (when (:strict opts)
    (throw (ex-info "Unknown val to openapi generator"
                    {:exoscale.ex/type :exoscale.ex/invalid
                     :form form})))
  (:unknown-spec-default opts))

(set-pred-schema! (s/def :s-exp.pact.json-schema.pred/num-compare
                    (s/or :count-1
                          (s/cat :op #{'= '< '> '<= '>= 'not=}
                                 :_ simple-symbol?
                                 :x any?)
                          :count-2 (s/cat :op #{'= '< '> '<= '>= 'not=}
                                          :x any?
                                          :_ simple-symbol?)))
                  (fn [[t {:keys [op x]}] _opts]

                    (case [t op]
                      ([:count-1 >=] [:count-2 <=]) {:minimum x :type "number"}
                      ([:count-1 <=] [:count-2 >=]) {:maximum x :type "number"}
                      ([:count-1 >] [:count-2 <]) {:minimum (inc x) :type "number"}
                      ([:count-1 <] [:count-2 >]) {:maximum (dec x) :type "number"}
                      ([:count-1 =] [:count-2 =]) {:const x}
                      ([:count-1 not=] [:count-2 not=]) {:not {:const x}})))

(s/def ::count+arg (s/spec (s/cat :_ #{'count} :sym simple-symbol?)))

(set-pred-schema! (s/def :s-exp-pact.pred/count-compare
                    (s/or :count-1
                          (s/cat :op #{'<= '< '> '>= '= 'not=}
                                 :_cnt ::count+arg
                                 :x number?)

                          :count-2
                          (s/cat :op #{'<= '< '> '>= '= 'not=}
                                 :x number?
                                 :_cnt ::count+arg)))
                  (fn [[t {:keys [op x]}] _opts]

                    (assoc (case [t op]
                             ([:count-1 =] [:count-2 =]) {:const x}
                             ([:count-1 not=] [:count-2 not=]) {:not {:const x}}
                             ([:count-1 <=] [:count-2 >=]) {:maxItems x}
                             ([:count-1 >=] [:count-2 <=]) {:minItems x}
                             ([:count-1 <] [:count-2 >]) {:maxItems (dec x)}
                             ([:count-1 >] [:count-2 <]) {:minItems (inc x)})
                           :type "array")))

(defn- set-json-schema!
  [spec json-schema & _opts]
  (swap! registry-ref
         assoc-in
         [:s-exp.pact.json-schema/schema spec]
         json-schema)
  json-schema)

(defn json-schema
  "Generate json-schema for `spec`"
  [spec & {:as opts}]
  (let [opts (into default-opts opts)]
    (set-json-schema! spec
                      (json-schema* spec opts)
                      opts)))

;; (:registry default-opts)
;; (s/def ::foo (s/keys :req [::bar]))
;; (s/def ::bar (s/keys :req [::baz]))
;; (s/def ::baz (s/coll-of ::s))
;; (s/def ::ss ::s)
;; (s/def ::ssss ::ss)
;; (with-format ::s "ip4")
;; (with-pattern ::s "^(\\([0-9]{3}\\))?[0-9]{3}-[0-9]{4}$")
;; (find-description ::ssss)
;; (find-format ::ssss)

;; (si/spec-ancestors ::ssss)

;; (gen ::s)
;; (gen ::baz)
;; (gen #{:a})
;; (gen `(s/map-of string? string?))
;; (prn (gen `(s/keys :req-un [::s (or ::baz (and ::s))])))
;; (gen `(s/map-of string? (s/coll-of (s/keys :req-un [::s]))))
;; (gen `boolean?)
;; (gen `(s/coll-of int?))
;; (def f (fn [x] (> x 10)))

;; (prn #'f)
;; ;; (gen `(s/merge (s/keys :req-un [::s]) (s/keys :req-un [::baz])))

;; (s/def ::ff (s/and (s/coll-of string?) (fn [x] (<= (count x)
;;                                                    10))))
;; (gen ::ff)
;; (gen `(s/or :s string? :u uuid?))

;; (gen `(s/int-in 0 10))

;; (s/def ::fname string?)
;; (s/def ::lname string?)
;; (s/def ::street string?)
;; (s/def ::city string?)

;; (s/def ::person (s/keys :req [::fname ::lname]))
;; (s/def ::address (s/keys :req [::street ::city]))

;; (s/valid? (s/merge ::person ::address))
;; (gen ::bar)

;; (s/def ::tag #{:a :b :c :d})
;; (s/def ::example-key keyword?)
;; (s/def ::different-key keyword?)

;; (defmulti tagmm :tag)
;; (defmethod tagmm :a [_] (s/keys :req-un [::tag ::example-key]))
;; (defmethod tagmm :default [_] (s/keys :req-un [::tag ::different-key]))

;; (s/def ::example (s/multi-spec tagmm :tag))

;; (gen ::example)
