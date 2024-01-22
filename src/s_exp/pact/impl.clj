(ns s-exp.pact.impl
  (:refer-clojure :exclude [derive vary-meta meta])
  (:require
   [clojure.spec.alpha :as s]
   [clojure.walk :as walk]))

;;; reg

(defn registry-meta
  "Returns metadata registry or metadata for spec `k`"
  ([registry-val] (get registry-val :s-exp.pact.json-schema/meta))
  ([registry-val k]
   (get (registry-meta registry-val) k)))

(defn registry-form
  "Returns registry form function for key `k`"
  [registry-val k]
  (get-in registry-val [:s-exp.pact.json-schema/forms k]))

(defn registry-ident
  "Returns registry ident value for key `k`"
  [registry-val k]
  (get-in registry-val [:s-exp.pact.json-schema/idents k]))

(defn find-schema
  "Find first schema generator in spec hierarchy"
  [registry-val spec-chain {:as opts :keys [idents forms preds]}]
  (let [registry-val
        (-> registry-val
            (update :s-exp.pact.json-schema/forms merge forms)
            (update :s-exp.pact.json-schema/idents merge idents)
            (update :s-exp.pact.json-schema/preds merge preds))
        opts (merge opts registry-val)]
    (reduce (fn resolve-schema* [_ x]
              (when-let [schema (cond
                                  (set? x)
                                  ((registry-form registry-val 's-exp.pact/enum-of)
                                   x opts)

                                  (sequential? x)
                                  ((registry-form registry-val (first x))
                                   (rest x) opts)

                                  (qualified-ident? x)
                                  (registry-ident registry-val x))]
                (reduced schema)))
            nil
            spec-chain)))

(defn find-key
  "Find first `prop` value in spec hierarchy for spec"
  [prop]
  (fn [registry-val spec-chain]
    (let [m (registry-meta registry-val)]
      (reduce (fn find-key* [_ k]
                (when (qualified-keyword? k)
                  (when-let [val (get-in m [k prop])]
                    (reduced val))))
              nil
              spec-chain))))

;;; Preds

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

(defn pred-conformer
  [pred {:as opts :s-exp.pact.json-schema/keys [preds]}]
  (reduce (fn match-conformer [_ [k f]]
            (let [match (s/conform k (abbrev pred))]
              (when-not (= :clojure.spec.alpha/invalid match)
                (reduced (f match opts)))))
          nil
          preds))

;;; Spec inspection

(defn accept-keyword [x]
  (when (qualified-keyword? x)
    x))

(defn accept-symbol [x]
  (when (qualified-symbol? x)
    x))

(defn accept-set [x]
  (when (set? x)
    x))

(defn accept-symbol-call [spec]
  (when (and (seq? spec)
             (symbol? (first spec)))
    spec))

(defn spec-form
  "Return the spec form or nil."
  [spec]
  (some-> spec s/get-spec s/form))

(defn spec-root
  "Determine the main spec root from a spec form."
  [spec]
  (let [spec-def (or (spec-form spec)
                     (accept-symbol spec)
                     (accept-symbol-call spec)
                     (accept-set spec))]
    (cond-> spec-def
      (qualified-keyword? spec-def)
      recur)))

(defn parent-spec
  "Look up for the parent coercer using the spec hierarchy."
  [k]
  (or (accept-keyword (s/get-spec k))
      (accept-keyword (spec-form k))))

(defn spec-chain
  "Determine the main spec root from a spec form."
  [spec]
  (loop [spec spec
         ret (cond-> [spec])]
    (let [p (or (parent-spec spec)
                (spec-form spec))]
      (if p
        (recur p (conj ret p))
        ret))))

;;; schemas impls

(defn string-schema
  ([] (string-schema {}))
  ([opts]
   (merge {:type "string"} opts)))

(defn array-schema
  ([] {:type "array"})
  ([opts]
   (merge (array-schema) opts)))
