(ns s-exp.pact.impl
  (:refer-clojure :exclude [derive vary-meta meta])
  (:require
   [clojure.spec.alpha :as s]
   [clojure.walk :as walk]
   [s-exp.pact.inspect :as si]))

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
  ((get-in opts [:s-exp.pact.json-schema/pred-schema k])
   match
   opts))

(defn pred-conformer
  [pred {:as opts :s-exp.pact.json-schema/keys [preds]}]
  (reduce (fn [_ [k f]]
            (let [match (s/conform k (abbrev pred))]
              (when-not (= :clojure.spec.alpha/invalid match)
                (reduced (f match opts)))))
          nil
          preds))

(defn registry-lookup
  [registry k f]
  (let [c (get registry k)]
    (if-let [x (f c)]
      x
      (when-let [parent (-> (si/parent-spec k) si/accept-keyword)]
        (recur registry parent f)))))

;;; schemas impls
(defn string-schema
  ""
  ([] (string-schema {}))
  ([opts]
   (merge {:type "string"} opts)))

(defn array-schema
  ([] {:type "array"})
  ([opts]
   (merge (array-schema) opts)))
