(ns s-exp.pact
  (:refer-clojure :exclude [derive vary-meta meta with-meta])
  (:require
   [clojure.spec.alpha :as s]
   [exoscale.ex :as ex]
   [s-exp.pact.impl :as impl]))

(s/def :s-exp.pact/options
  (s/keys :opt-un [:s-exp.pact.option/property-key-fn
                   :s-exp.pact.option/gen-only-first-and-arg
                   :s-exp.pact.option/strict
                   :s-exp.pact.option/unknown-spec-default]))

(s/def :s-exp.pact.option/property-key-fn ifn?)
(s/def :s-exp.pact.option/gen-only-first-and-arg boolean?)
(s/def :s-exp.pact.option/strict boolean?)
(s/def :s-exp.pact.option/unknown-spec-default any?)

(def default-opts
  {:property-key-fn name
   :gen-only-first-and-arg false
   :strict true ; will throw on unknown conversion
   :unknown-spec-default nil})

(defonce registry-ref
  (atom #:s-exp.pact.json-schema{:meta {}
                                 :idents {}
                                 :forms {}
                                 :preds {}}))

(s/def :s-exp.pact.json-schema/schema map?)
(s/def :s-exp.pact.json-schema/forms (s/map-of symbol? ifn?))
(s/def :s-exp.pact.json-schema/idents (s/map-of qualified-ident? :s-exp.pact.json-schema/schema))
(s/def :s-exp.pact.json-schema/preds (s/map-of qualified-keyword? ifn?))

(defn registry
  "Returns registry"
  ([]
   @registry-ref)
  ([k]
   (get (registry) k)))

(defn register-form!
  "Registers a `form` for generation. Upon encountering that form (by matching its
  against first element or sequential symbolic spec values), `json-schema` will
  then call `f` against its arguments for generation. Returns form passed as argument"
  [form f]
  (swap! registry-ref update :s-exp.pact.json-schema/forms
         assoc form f)
  form)

(defn register-ident!
  "Registers an `ident` for generation. Upon encountering a qualified
  symbol/keyword matching an `ident` in the registry it will return `x` as
  generated value. Returns the `ident` passed as argument"
  [ident x]
  (swap! registry-ref update :s-exp.pact.json-schema/idents
         assoc ident x)
  ident)

(def find-id
  "Find first `$id` value in spec hierarchy for spec"
  (impl/find-key :$id))

(def find-title
  "Find first `title` value in spec hierarchy for spec"
  (impl/find-key :title))

(def find-description
  "Find first `description` value in spec hierarchy for spec"
  (impl/find-key :description))

(def find-format
  "Find first `format` value in spec hierarchy for spec"
  (impl/find-key :format))

(def find-pattern
  "Find first `pattern` value in spec hierarchy for spec"
  (impl/find-key :pattern))

(def find-schema
  "Find first `schema` value in spec hierarchy for spec"
  impl/find-schema)

(defn vary-meta
  "Like `clojure.core/vary-meta but on spec `k` metadata"
  [k f & args]
  (swap! registry-ref
         update-in
         [:s-exp.pact.json-schema/meta k]
         #(apply f % args))
  k)

(defn with-meta
  "Sets metadata for `spec` "
  [spec x]
  (vary-meta spec (constantly x)))

(defn meta
  "Returns `spec` metadata"
  [spec]
  (get-in @registry-ref [:s-exp.pact.json-schema/meta spec]))

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
  "Add `format` to spec.
  See https://json-schema.org/understanding-json-schema/reference/string#built-in-formats"
  [k fmt]
  (assoc-meta k :format fmt))

(defn with-pattern
  "Add `pattern` to spec"
  [k p]
  (assoc-meta k :pattern p))

(defn json-schema
  "Generate json-schema for `spec`.
  `opts` support:

  * `property-key-fn` - function that will convert the spec keys to json-schema
  keys - defaults to `name`

  * `strict` - whether to throw or not upon encountering unknown values for
  conversion - defaults to `true`

  * `gen-only-first-and-arg` - whether to only attempt generate the first
  predicate of `s/and` - defaults to `false`

  * `unknown-spec-default` - value to be used for unknown values for conversion
  - defaults to `nil`"
  [k & {:as opts
        :keys [property-key-fn strict gen-only-first-and-arg
               unknown-spec-default]}]
  (let [opts (into default-opts opts)
        spec-chain (impl/spec-chain k)
        registry-val (registry)
        ret (or (find-schema registry-val
                             spec-chain
                             opts)
                (when (:strict opts)
                  (throw (ex-info "Unknown val to json-schema generator"
                                  {:exoscale.ex/type :s-exp.pact/unknown-val
                                   :spec k})))
                (:unknown-spec-default opts))
        desc (find-description registry-val spec-chain)
        fmt (find-format registry-val spec-chain)
        pattern (find-pattern registry-val spec-chain)
        id (find-id registry-val spec-chain)
        title (find-title registry-val spec-chain)]
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

(ex/derive :s-exp.pact/unknown-val :exoscale.ex/invalid)

;; idents

(register-ident! `nil? {:type "null"})
(register-ident! `string? (impl/string-schema))
(register-ident! `keyword? (impl/string-schema))
(register-ident! `qualified-keyword? (impl/string-schema))
(register-ident! `simple-keyword? (impl/string-schema))
(register-ident! `ident? (impl/string-schema))
(register-ident! `qualified-ident? (impl/string-schema))
(register-ident! `simple-ident? (impl/string-schema))
(register-ident! `symbol? (impl/string-schema))
(register-ident! `qualified-symbol? (impl/string-schema))
(register-ident! `simple-symbol? (impl/string-schema))
(register-ident! `uuid? (impl/string-schema :format "uuid"))
(register-ident! `uri? (impl/string-schema :format "uri"))
(register-ident! `boolean? {:type "boolean"})
(register-ident! `true? {:const true})
(register-ident! `false? {:const false})
(register-ident! `inst? (impl/string-schema :format "date-time"))
(register-ident! `any? (impl/object-schema))
(register-ident! `some? (impl/object-schema))
(register-ident! `map? (impl/object-schema))
(register-ident! `number? (impl/number-schema))
(register-ident! `int? (impl/int-schema :format "int64"))
(register-ident! `pos? (impl/int-schema :format "int64" :minimum 0))
(register-ident! `integer? (impl/int-schema :format "int64"))
(register-ident! `nat-int? (impl/int-schema :format "int64" :minimum 0))
(register-ident! `pos-int? (impl/int-schema :format "int64" :minimum 1))
(register-ident! `neg-int? (impl/int-schema :format "int64" :maximum -1))
(register-ident! `neg? (impl/int-schema :format "int64" :maximum -1))
(register-ident! `float? (impl/number-schema :format "float"))
(register-ident! `double? (impl/number-schema :format "double"))
(register-ident! `decimal? (impl/number-schema :format "double"))
(register-ident! `rational? (impl/number-schema :format "double"))
(register-ident! `zero? {:const 0})
(register-ident! `coll? (impl/array-schema))
(register-ident! `empty? (impl/array-schema :maxItems 0 :minItems 0))
(register-ident! `vector? (impl/array-schema))
(register-ident! `list? (impl/array-schema))
(register-ident! `set? (impl/array-schema :uniqueItems true))
(register-ident! `sequential? (impl/array-schema))

;;; forms

(defn- every-form-gen*
  [[spec & {:as spec-opts :keys [max-count min-count length kind]}] opts]
  (let [distinct (or (:distinct spec-opts) (= kind `set?))]
    (cond-> (impl/array-schema :items (json-schema spec opts))
      length
      (assoc :minItems length
             :maxItems length)
      max-count
      (assoc :maxItems max-count)
      min-count
      (assoc :minItems min-count)
      distinct
      (assoc :uniqueItems true))))

(register-form! `s/every every-form-gen*)
(register-form! `s/coll-of every-form-gen*)

(register-form! `s/cat
                (fn [pairs opts]
                  (impl/array-schema
                   :items
                   {:anyOf
                    (map (fn [[_k spec]]
                           (json-schema spec opts))
                         (partition-all 2 pairs))})))

(register-form! `s/*
                (fn [spec opts]
                  (impl/array-schema
                   :items (map (fn [spec] (json-schema spec opts)) spec))))

(register-form! `s/+
                (fn [more opts]
                  (impl/array-schema
                   :items (map (fn [spec] (json-schema spec opts)) more)
                   :minItems 1)))

(register-form! `s/?
                (fn [more opts]
                  (impl/array-schema
                   :items (map (fn [spec] (json-schema spec opts)) more)
                   :minItems 0)))

(register-form!
 `s/map-of
 (fn [[_ val-spec] opts]
   {:type "object"
    :patternProperties {"*" (json-schema val-spec opts)}}))

(register-form!
 `s/tuple
 (fn [[_ & specs] opts]
   (impl/array-schema
    :items (map (fn [spec] (json-schema spec opts))
                specs))))

(defn- parse-s-keys
  [form]
  (-> (apply hash-map form)
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
                 [(property-key-fn k) (json-schema k opts)]))
          specs)))

(register-form!
 `s/keys
 (fn [form {:as opts :keys [property-key-fn]}]
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
                                           [:req-un :req])))))))

(register-form!
 `s/nilable
 (fn [[form] opts]
   {:anyOf [{:type "null"}
            (json-schema form opts)]}))

(register-form!
 `s/multi-spec
 (fn [[mm tag-key] opts]
   (let [f (resolve mm)]
     {:anyOf (into []
                   (comp
                    (map (fn extract-spec [[dispatch-val _spec]]
                           (impl/spec-root (s/form (f {tag-key dispatch-val})))))
                    (map (fn get-json-schema [k]
                           (json-schema k opts))))
                   (methods @f))})))

(register-form!
 `enum-of
 (fn [values _opts]
   {:enum values}))

(register-form!
 `s/int-in
 (fn [[min max] _opts]
   {:type "integer"
    :minimum min
    :maximum (dec max)}))

(register-form!
 `s/and
 (fn [[& forms] {:as opts :keys [gen-only-first-and-arg]}]
   {:allOf (into []
                 (keep #(json-schema % opts))
                 (if gen-only-first-and-arg
                   [(first forms)]
                   forms))}))

(register-form!
 `s/merge
 (fn [[& forms] opts]
   {:allOf (into []
                 (map #(json-schema % opts))
                 forms)}))

(defn- or-schema
  [[& forms] opts]
  {:anyOf (into []
                (comp
                 (partition-all 2)
                 (map #(json-schema (second %) opts)))
                forms)})

(register-form! `s/or or-schema)
(register-form! `s/alt or-schema)

;;

(defn register-pred!
  "Sets `conformer` and `schema-fn` for predicate parser.
  If a conformer matches, the bindings we get from the s/conform result will be
  passed to `schema-fn` in order to generate an appropriate json-schema value
  for the predicate."
  ([k schema-fn _opts]
   (swap! registry-ref
          (fn [registry-val]
            (assoc-in registry-val
                      [:s-exp.pact.json-schema/preds k]
                      schema-fn)))
   k)
  ([k schema-fn]
   (register-pred! k schema-fn default-opts)))

(defn- parse-fn
  [args fn-body opts]
  (or (impl/pred-conformer fn-body opts)
      (if (:strict opts)
        (throw (ex-info "Unknown predicate to json-schema generator"
                        {:exoscale.ex/type :s-exp.pact/unknown-pred
                         :args args
                         :body fn-body}))
        (json-schema (:unknown-spec-default opts) opts))))

(ex/derive :s-exp.pact/unknown-pred :exoscale.ex/invalid)

(register-form!
 `clojure.core/fn
 (fn [[args form] opts]
   (parse-fn args form opts)))

;;; preds

(register-pred! (s/def :s-exp.pact.json-schema.pred/num-compare
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

(register-pred! (s/def :s-exp-pact.pred/count-compare
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
