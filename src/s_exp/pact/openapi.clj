(ns s-exp.pact.openapi
  (:require [clojure.spec.alpha :as s]
            [s-exp.pact.json-schema :as json-schema]))

(s/def ::foo (s/keys :req [::bar]))
(s/def ::bar (s/keys :req [::baz]))
(s/def ::baz (s/coll-of ::s))
(s/def ::s string?)

(json-schema/gen ::foo)

;; (gen {:components {:schemas {:job ::foo}}})

(defn schemas* [schemas]
  (reduce-kv (fn [m k spec-key]
               (assoc m k (json-schema/gen spec-key)))
             {}
             schemas))

(defn components*
  [{:as _components :keys [schemas]}]
  (cond-> {}
    schemas
    (assoc :schemas (schemas* schemas))))

(defn servers*
  [{:as _servers}]
  {})

(defn paths*
  [{:as _paths}]
  {})

(defn gen
  [{:as doc :keys [info tags components servers paths x-a]}]
  (cond-> {:openapi "3.1.0"
    ;; :info (prefix-extensions info :extensions)
    ;; :tags (generate-tags tags)
    ;; :components {:schemas (generate-schemas resources)}
    ;; :servers servers
    ;; :extensions extensions
    ;; :paths (reduce-kv assoc-path {} (into {} commands))
           }
    info
    (assoc :info info)

    tags
    (assoc :tags tags)

    (seq components)
    (assoc :components (components* components))

    servers
    (assoc :servers (servers* servers))

    paths
    (assoc :paths (paths* paths))

    x-a
    (assoc :x-a x-a)))
