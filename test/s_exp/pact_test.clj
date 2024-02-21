(ns s-exp.pact-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [is deftest are]]
            [exoscale.ex.test]
            [s-exp.pact :as p]
            [s-exp.pact.impl :as impl]))

(s/check-asserts true)
(set-validator! p/registry-ref
                (fn [x]
                  (s/assert (s/keys) x)))

(deftest test-simple-preds
  (are [spec schema] (= schema (p/json-schema spec))
    `string? {:type "string"}
    `keyword? {:type "string"}
    `number? {:type "number"}
    `int? {:type "integer" :format "int64"}
    `integer? {:type "integer" :format "int64"}
    `pos-int? {:type "integer" :format "int64" :minimum 1}
    `nat-int? {:type "integer" :format "int64" :minimum 0}
    `neg-int? {:type "integer" :format "int64" :maximum -1}
    `float? {:type "number" :format "float"}
    `double? {:type "number" :format "double"}
    `boolean? {:type "boolean"}
    `inst? {:type "string" :format "date-time"}
    `any? {:type "object"}
    `coll? {:type "array"}
    `list? {:type "array"}
    `sequential? {:type "array"}
    `vector? {:type "array"}
    `map? {:type "object"}
    `uuid? {:type "string" :format "uuid"}
    `nil? {:type "null"}))

(deftest test-composites
  (are [spec schema] (= schema (p/json-schema spec))
    #{:a :b} {:enum #{:a :b}}
    `(s/and number? int?) {:allOf [{:type "number"} {:type "integer" :format "int64"}]}
    `(s/or :num number? :int int?) {:anyOf [{:type "number"} {:type "integer" :format "int64"}]}
    `(s/alt :num number? :int int?) {:anyOf [{:type "number"} {:type "integer" :format "int64"}]}
    `(s/coll-of string?) {:type "array", :items {:type "string"}}
    `(s/coll-of string? :max-count 3) {:type "array", :items {:type "string"} :maxItems 3}
    `(s/map-of any? string?) {:type "object", :patternProperties {"*" {:type "string"}}}
    `(s/map-of int? string?) {:type "object", :patternProperties {"*" {:type "string"}}}
    `(s/cat :foo (s/* string?)) {:type "array" :items {:anyOf [{:items [{:type "string"}], :type "array"}]}}
    `(s/int-in 0 3) {:type "integer", :minimum 0, :maximum 2}
    `(s/nilable string?) {:anyOf [{:type "null"} {:type "string"}]}))

(s/def ::foo string?)
(s/def ::bar keyword?)
(s/def ::baz int?)

(deftest test-s-keys
  (are [spec schema] (= schema (p/json-schema spec))
    `(s/keys :req-un [::foo ::bar] :opt-un [::baz])
    {:type "object",
     :properties
     {"foo" {:type "string"},
      "bar" {:type "string"},
      "baz" {:type "integer", :format "int64"}},
     :required ["foo" "bar"]}

    `(s/keys :req [::foo ::bar] :opt [::baz])
    {:type "object",
     :properties
     {"foo" {:type "string"},
      "bar" {:type "string"},
      "baz" {:type "integer", :format "int64"}},
     :required ["foo" "bar"]}

    `(s/keys :req-un [(or ::foo (and ::bar ::baz))])
    {:type "object",
     :properties
     {"foo" {:type "string"},
      "bar" {:type "string"},
      "baz" {:type "integer", :format "int64"}},
     :required ["foo" "bar" "baz"]}

    `(s/merge (s/keys :req-un [::foo]) (s/keys :req-un [::bar])
              (s/keys :opt-un [::baz]))
    {:allOf
     [{:type "object",
       :properties {"foo" {:type "string"}},
       :required ["foo"]}
      {:type "object",
       :properties {"bar" {:type "string"}},
       :required ["bar"]}
      {:type "object",
       :properties {"baz" {:type "integer", :format "int64"}}}]}))

(s/def ::tag #{:a :b :c :d})
(s/def ::example-key keyword?)
(s/def ::different-key keyword?)
(defmulti tagmm :tag)
(defmethod tagmm :a [_] (s/keys :req-un [::tag ::example-key]))
(defmethod tagmm :default [_] (s/keys :req-un [::tag ::different-key]))
(s/def ::ms (s/multi-spec tagmm :tag))

(deftest test-s-multi-spec
  (are [spec schema] (= schema (p/json-schema spec))
    ::ms {:anyOf
          [{:type "object",
            :properties
            {"tag" {:enum #{:c :b :d :a}}, "different-key" {:type "string"}},
            :required ["tag" "different-key"]}
           {:type "object",
            :properties
            {"tag" {:enum #{:c :b :d :a}}, "example-key" {:type "string"}},
            :required ["tag" "example-key"]}]}))

(deftest test-predicates-math
  (s/def ::f1 (s/and number? (fn [x] (>= x 10))))
  (s/def ::f1' (s/and number? #(>= % 10)))
  (s/def ::f2 (s/and number? (fn [x] (> x 10))))
  (s/def ::f3 (s/and number? (fn [x] (<= x 10))))
  (s/def ::f4 (s/and number? (fn [x] (< x 10))))
  (s/def ::f5 (s/and number? (fn [x] (= 10 x))))
  (s/def ::f6 (s/and number? (fn [x] (not= 10 x))))

  (s/def ::f7 (s/and number? (fn [x] (>= 10 x))))
  (s/def ::f8 (s/and number? (fn [x] (> 10 x))))
  (s/def ::f9 (s/and number? (fn [x] (<= 10 x))))
  (s/def ::f10 (s/and number? (fn [x] (< 10 x))))
  (s/def ::f11 (s/and number? (fn [x] (= x 10))))
  (s/def ::f12 (s/and number? (fn [x] (not= x 10))))
  (are [spec schema] (= schema (p/json-schema spec))
    ::f1 {:allOf [{:type "number"} {:minimum 10, :type "number"}]}
    ::f1' {:allOf [{:type "number"} {:minimum 10, :type "number"}]}
    ::f2 {:allOf [{:type "number"} {:minimum 11, :type "number"}]}
    ::f3 {:allOf [{:type "number"} {:maximum 10, :type "number"}]}
    ::f4 {:allOf [{:type "number"} {:maximum 9, :type "number"}]}
    ::f5 {:allOf [{:type "number"} {:const 10}]}
    ::f6 {:allOf [{:type "number"} {:not {:const 10}}]}
    ::f7 {:allOf [{:type "number"} {:maximum 10, :type "number"}]}
    ::f8 {:allOf [{:type "number"} {:maximum 9, :type "number"}]}
    ::f9 {:allOf [{:type "number"} {:minimum 10, :type "number"}]}
    ::f10 {:allOf [{:type "number"} {:minimum 11, :type "number"}]}
    ::f11 {:allOf [{:type "number"} {:const 10}]}
    ::f12 {:allOf [{:type "number"} {:not {:const 10}}]}))

(deftest pred-strictness
  (s/def ::unknown-pred (s/and string? (fn [x] :stuff)))
  (is (thrown-ex-info-type? :s-exp.pact/unknown-pred (p/json-schema ::unknown-pred)))
  (is (= {:allOf [{:type "string"}]} (p/json-schema ::unknown-pred {:strict false}))))

(defmacro string-of [& _])

(deftest form-strictness
  (s/def ::unknown-form (s/and string? (string-of string?)))
  (is (thrown-ex-info-type? :s-exp.pact/unknown-val (p/json-schema ::unknown-form)))
  (is (= {:allOf [{:type "string"}]} (p/json-schema ::unknown-form {:strict false}))))

(deftest ident-strictness
  (s/def ::unknown-ident (s/and ::xxx ::yyy))
  (is (thrown-ex-info-type? :s-exp.pact/unknown-val (p/json-schema ::unknown-ident)))
  (is (= {:allOf []} (p/json-schema ::unknown-ident {:strict false})))
  (is (= {:allOf [{:type "object"} {:type "object"}]}
         (p/json-schema ::unknown-ident {:strict false
                                         :unknown-spec-default {:type "object"}}))))

(deftest s-and-first-arg
  (s/def ::s-and-first (s/and string? (string-of string?)))
  (is (= {:allOf [{:type "string"}]}
         (p/json-schema ::s-and-first {:gen-only-first-and-arg true}))))

(deftest meta-test
  (let [title "test"
        pattern "pattern"]
    (-> (s/def ::meta-test string?)
        (p/with-title title)
        (p/with-id "id")
        (p/with-pattern pattern)
        (p/with-description "description")
        (p/with-format "format"))
    (s/def ::meta-test2 ::meta-test)
    (s/def ::meta-test3 ::meta-test2)

    (is (= (p/find-title (p/registry) (impl/spec-chain ::meta-test))
           title))
    (is (= (p/find-title (p/registry) (impl/spec-chain ::meta-test2))
           title))
    (is (= (p/find-title (p/registry) (impl/spec-chain ::meta-test3))
           title))
    (is (= (p/find-pattern (p/registry) (impl/spec-chain ::meta-test3))
           pattern))

    (let [schema {:extra "yolo"}]
      (is (= (:extra schema)
             (:extra (p/json-schema ::meta-test
                                    {:idents {::meta-test schema}}))))
      (is (= (:extra schema)
             (:extra (p/json-schema ::meta-test2
                                    {:idents {::meta-test schema}}))))
      (is (= {:stuff 1} (p/json-schema `(s/coll-of string?)
                                       {:forms {`s/coll-of (fn [_ _] {:stuff 1})}})))

      (is (= {:stuff 1}
             (p/json-schema `(s/coll-of string?)
                            {:forms {`s/coll-of (fn [_ _] {:stuff 1})}})
             (p/json-schema `(s/coll-of string?)
                            :forms {`s/coll-of (fn [_ _] {:stuff 1})}))))))
