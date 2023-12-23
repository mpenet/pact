(ns s-exp.pact.json-schema
  (:refer-clojure :exclude [derive vary-meta meta])
  (:require
   [clojure.spec.alpha :as s]
   [clojure.walk :as walk]
   [s-exp.pact.inspect :as si]))

(defonce registry-ref
  (atom #:s-exp.pact.json-schema{:meta {}
                                 :schema {}
                                 :pred-conformers #{}
                                 :pred-schema {}}))

(defn vary-meta
  [k f & args]
  (swap! registry-ref update-in
         [:s-exp.pact.json-schema/meta k]
         #(apply f % args)))

(defn meta
  ([] (get @registry-ref :s-exp.pact.json-schema/meta))
  ([k]
   (get-in @registry-ref [:s-exp.pact.json-schema/meta k])))

(defn with-description
  "Adds description to spec"
  [k description]
  (vary-meta k assoc :description description))

(defn with-format [k fmt]
  (vary-meta k assoc :format fmt))

(defn find-description
  [k]
  (si/registry-lookup (meta) k :description))

(defn find-schema
  [k]
  (si/registry-lookup (meta) k :schema))

(defn find-format
  [k]
  (si/registry-lookup (meta) k :format))

(declare schema)

(defn strip-core
  [sym]
  (cond-> sym
    (= (namespace sym) "clojure.core")
    (-> name symbol)))

(defn abbrev [form]
  (cond->> form
    (seq? form)
    (walk/postwalk (fn [form]
                     (let [qs? (qualified-symbol? form)]
                       (cond
                         ;; just treat */% as %
                         (and qs? (= "%" (name form)))
                         (symbol "%")

                         ;; it's could be a core symbol, in that case remove ns
                         qs?
                         (strip-core form)

                         (and (seq? form)
                              (contains? #{'fn 'fn*} (first form)))
                         (last form)
                         :else form))))))

(defn pred-schema
  [k match opts]
  ((get-in @registry-ref [:s-exp.pact.json-schema/pred-schema k])
   match
   opts))

(defn pred-conformer
  [conformers pred opts]
  (reduce (fn [_ k]
            (when-let [match (s/conform k (abbrev pred))]
              (when (not= match :clojure.spec.alpha/invalid)
                (reduced (pred-schema k match opts)))))
          nil
          conformers))

(def default-opts
  {:property-key-fn name
   :strict? false ; will throw on unknown conversion
   :unknown-spec-default {:type "object"}})

(defn set-pred-conformer!
  ([k schema-fn opts]
   (swap! registry-ref
          (fn [registry-val]
            (-> registry-val
                (update :s-exp.pact.json-schema/pred-conformers conj k)
                (assoc-in [:s-exp.pact.json-schema/pred-schema k] schema-fn)))))
  ([k schema-fn]
   (set-pred-conformer! k schema-fn default-opts)))

(defn generate [k opts]
  (let [ret
        (if-let [schema (find-schema k)]
          schema
          (schema (si/spec-root k) opts))
        ;; ancestors (si/spec-ancestors k)
        desc (find-description k)
        fmt (find-format k)]
    (cond-> ret
      desc
      (assoc :description desc)
      fmt
      (assoc :format fmt))))

(def hierarchy (atom (make-hierarchy)))

(defn derive
  "Like clojure.core/derive but scoped on our `hierarchy`"
  [tag parent]
  (swap! hierarchy
         clojure.core/derive tag parent))

;; (def schema nil)
(defmulti schema
  (fn [form _opts]
    (cond
      (set? form) `enum
      (sequential? form) (first form)
      :else form))
  :hierarchy hierarchy)

(defmethod schema `s/coll-of
  [form opts]
  {:type "array"
   :allOf (generate (second form) opts)})

(defmethod schema `coll?
  [_form _opts]
  {:type ""})

(derive `list? `coll?)
(derive `s/cat `coll?)

(defmethod schema `s/map-of
  [[_ _ val-spec] opts]
  {:type "object"
   :patternProperties {"*" (generate val-spec opts)}})

(def s-keys-k [:req-un :req :opt :opt-un])

(defn parse-s-keys [form]
  (-> (apply hash-map (rest form))
      (update-vals #(->> %
                         flatten
                         ;; hackish, but will do for now
                         (remove #{`or `and})
                         (distinct)))))

(defn keys->properties [pk {:as opts :keys [property-key-fn]}]
  (let [specs (eduction (mapcat (comp val))
                        (select-keys pk s-keys-k))]
    (into {}
          (map (fn [k]
                 [(property-key-fn k) (generate k opts)]))
          specs)))

(defmethod schema `s/keys
  [form {:as opts :keys [property-key-fn]}]
  (let [keys' (parse-s-keys form)]
    {:type "object"
     :properties (keys->properties keys' opts)
     :required (into []
                     (comp (mapcat val)
                           (map property-key-fn))
                     (select-keys keys'
                                  [:req-un :req]))}))

(defmethod schema `nil?
  [form opts]
  {:type "null"})

(defmethod schema `s/nilable
  [[_ form] opts]
  {:oneOf [{:type "null"}
           (generate form opts)]})

(defmethod schema `s/multi-spec
  [[_ mm tag-key] opts]
  (let [f (resolve mm)
        methods (methods @f)]
    {:oneOf (into []
                  (comp
                   (map (fn extract-spec [[dispatch-val _spec]]
                          (si/spec-root (s/form (f {tag-key dispatch-val})))))
                   (map (fn get-json-schema [k]
                          (generate k opts))))
                  (methods @f))}))

(defmethod schema `enum
  [values opts]
  {:enum values})

(defmethod schema `string?
  [form opts]
  {:type "string"})

(derive `keyword? `string?)

(defmethod schema `uuid?
  [form opts]
  {:type "string" :format "uuid"})

(defmethod schema `int?
  [form opts]
  {:type "integer" :format "int64"})

(defmethod schema `nat-int?
  [form opts]
  {:type "integer"
   :format "int64"
   :minimum 0})

(defmethod schema `pos-int?
  [form opts]
  {:type "integer"
   :format "int64"
   :minimum 1})

(defmethod schema `neg-int?
  [form opts]
  {:type "integer"
   :maximum -1})

(derive `int? `integer?)

(defmethod schema `s/int-in
  [[_ min max] opts]
  {:type "integer"
   :mininum min
   :maximum max
   :exclusiveMaximum true})

(defmethod schema `number?
  [form opts]
  {:type "number"})

(derive `float? `number?)
(derive `double? `number?)

(defmethod schema `boolean?
  [form opts]
  {:type "boolean"})

(defmethod schema `inst?
  [form opts]
  {:type "string" :format "date-time"})

(defmethod schema `any?
  [form opts]
  {:type "object"})

(derive `map? `any?)

(defmethod schema `s/and
  [[_ & forms] opts]
  {:allOf (into []
                (map #(generate % opts))
                forms)})

(derive `s/merge `s/and)

(defmethod schema `s/or
  [[_ & forms] opts]
  {:anyOf (into []
                (comp
                 (partition-all 2)
                 (map #(generate (second %) opts)))
                forms)})

;;

(defn set-pred-schema!
  [spec-key schema-fn]
  (set-pred-conformer! spec-key schema-fn))

(defn parse-fn
  [form {:as opts}]
  (let [{:s-exp.pact.json-schema/keys [pred-conformers]} @registry-ref]
    (or (pred-conformer pred-conformers form opts)
        {:type "object" :pact "pred"})))

(defmethod schema `clojure.core/fn
  [form opts]
  (parse-fn form opts))

(defmethod schema :default
  [form opts]
  (when (:strict? opts)
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
                  (fn [[_ {:keys [op x]}] _opts]
                    (merge (case op
                             >= {:minimum x}
                             <= {:maximum x}
                             > {:minimum x
                                :exclusiveMinimum true}
                             < {:maximum x
                                :exclusiveMaximum true}
                             = {:const x}
                             not= {:not {:const x}})
                           {:type "number"})))

(s/def ::count+arg (s/spec (s/cat :_ #{'count} :sym simple-symbol?)))

(set-pred-schema! (s/def :s-exp-pact.pred/gte-count
                    (s/or :count-1
                          (s/cat :op #{'<= '< '> '>= '= 'not=}
                                 :x number?
                                 :_cnt ::count+arg)
                          :count-2
                          (s/cat :op #{'<= '< '> '>= '= 'not=}
                                 :_cnt ::count+arg
                                 :x number?)))
                  (fn [[_ {:keys [op x]}] _opts]
                    (assoc (case op
                             = {:const x}
                             not= {:not {:const x}}
                             <= {:maxItems x}
                             >= {:minItems x}
                             < {:maxItems (dec x)}
                             > {:minItems (inc x)})
                           :type "array")))

(defn set-json-schema!
  [spec json-schema & _opts]
  (swap! registry-ref
         assoc-in
         [:s-exp.pact.json-schema/schema spec]
         json-schema)
  json-schema)

(defn gen
  [spec & {:as opts}]
  (let [opts (into default-opts opts)]
    (set-json-schema! spec
                      (generate spec opts)
                      opts)))

;; (:registry default-opts)
(s/def ::foo (s/keys :req [::bar]))
(s/def ::bar (s/keys :req [::baz]))
(s/def ::baz (s/coll-of ::s))
(s/def ::ss ::s)
(s/def ::ssss ::ss)
(with-format ::s "ip4")
(find-description ::ssss)
(find-format ::ssss)

;; (si/spec-ancestors ::ssss)

(gen ::s)
(gen ::baz)
(gen #{:a})
(gen `(s/map-of string? string?))
(prn (gen `(s/keys :req-un [::s (or ::baz (and ::s))])))
(gen `(s/map-of string? (s/coll-of (s/keys :req-un [::s]))))
(gen `boolean?)
(gen `(s/coll-of int?))
(def f (fn [x] (> x 10)))

(prn #'f)
;; (gen `(s/merge (s/keys :req-un [::s]) (s/keys :req-un [::baz])))

(s/def ::ff (s/and (s/coll-of string?) (fn [x] (<= (count x)
                                                   10))))
(gen ::ff)
(gen `(s/or :s string? :u uuid?))

(gen `(s/int-in 0 10))

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
