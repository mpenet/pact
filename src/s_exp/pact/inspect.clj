(ns s-exp.pact.inspect
  (:require [clojure.spec.alpha :as s]))

(defn- accept-keyword [x]
  (when (qualified-keyword? x)
    x))

(defn- accept-symbol [x]
  (when (qualified-symbol? x)
    x))

(defn- accept-set [x]
  (when (set? x)
    x))

(defn- accept-symbol-call [spec]
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

(defn spec-ancestors
  [k]
  (loop [k k
         ret [k]]
    (if-let [parent (-> (parent-spec k) accept-keyword)]
      (recur parent (conj ret parent))
      ret)))

(defn registry-lookup
  [registry k f]
  (let [c (get registry k)]
    (if-let [x (f c)]
      x
      (when-let [parent (-> (parent-spec k) accept-keyword)]
        (recur registry parent f)))))
