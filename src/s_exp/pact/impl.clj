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
  [registry-val k match opts]
  ((get-in registry-val [:s-exp.pact.json-schema/pred-schema k])
   match
   opts))

(defn pred-conformer
  [registry-val conformers pred opts]
  (reduce (fn [_ k]
            (when-let [match (s/conform k (abbrev pred))]
              (when (not= match :clojure.spec.alpha/invalid)
                (reduced (pred-schema registry-val k match opts)))))
          nil
          conformers))

(defn registry-lookup
  [registry k f]
  (let [c (get registry k)]
    (if-let [x (f c)]
      x
      (when-let [parent (-> (si/parent-spec k) si/accept-keyword)]
        (recur registry parent f)))))

;;; schemas impls
