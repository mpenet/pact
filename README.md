# pact

/!\ WIP - everything is subject to change

Generate json-schema  from clojure specs.

# Rationale

Specs allow us to define contracts, why not re-using these to **generate
json-schema**. Spec forms are just "data", should be easy right?

Well, there are a few challenges:
* specs can be defined as arbitrary predicates
* specs can also be aliases to other specs
* specs can be the result of composition of other specs via s/and s/merge
  s/multi-spec s/keys etc
* specs can be *parameterized* (kind of, think `s/coll-of`, `s/int-in` & co)
* specs have no metadata, that makes adding features json-schema supports but
  spec doesn't a bit complicated

How `pact` attempts to handle these:

* **spec forms/composition of specs**: schemas can be inferred for all
  clojure.spec forms (s/and & co). In the cases where we cannot infer the schema
  we provide ways for you to specify what to do. We also provide ways to extend
  what we generate out of the box.
  
* **spec alias chains**: we ensure that alias chains are understood and walk them
  up trying to find the first spec key that will allow json-schema
  generation. For instance if you have a spec ::foo that is an alias to ::bar
  that is itself an alias to ::baz that is a `string?`, trying to generate
  json-schema for ::foo will check them in order until it finds enough
  information to do so (from ::baz definition).
  
* **metadata**: we have a few helpers that allow you to specify/override
  `title`, `description`, `format`, `pattern`, `$id` on top of existing specs,
  that will later show up in the json-schema for these. They also understand
  spec aliases and pick up the first walking back the spec alias chain.  

* **arbitrary predicates**: Predicate forms parsing is done using spec
  (conform), with conform, that's all you need to know to do it yourself. We
  conform predicate forms against a set of conformers we hold internally (that
  you can extend yourself), so that you can destructure from the conform call
  (ex grab argument values, or anything really) in order to then generate the
  appropriate openapi data via a supplied function. `pact` comes with a number
  of useful predicate parsers that allow to generate correct schemas for common
  cases (numercic comparaisons, length bounds and so on).
  
* Enable to register new generators, either globally or on per-call basis.  

By default pact is **strict**, it will throw at generation time if it cannot
infer the json-schema for a spec, but it will allow you to specify the missing
bits.

It can also function in non strict mode where unknowns generate whatever you
specify by default, or just skip what it can't infer in some cases.

You can also tune the interpretation of some forms to be less strict, for
instance having only the first component of a `s/and` to be taken into account
and a few others like this. But by default we try to cover the full spec.

We **do not** provide an openapi generator, if you want to generate openapi
using `pact` it's very easy to do so, there's no need for an extra lib layer to
do so. That also gives you more control over the way you manage $refs, paths and
other openapi details.

## Examples

```clojure

(require '[s-exp.pact :as p])
(require '[clojure.spec.alpha :as s])

;; simple specs
(p/json-schema `(s/and number? int?))
=> {:allOf [{:type "number"} {:type "integer" :format "int64"}]}

(p/json-schema `(s/or number? int?))
=> {:oneOf [{:type "number"} {:type "integer" :format "int64"}]}

(p/json-schema `(s/coll-of string? :max-count 3))
=> {:type "array", :items {:type "string"} :maxItems 3}

(s/def ::foo string?)
(s/def ::bar keyword?)
(s/def ::baz int?)

;; maps
(s/def ::thing (s/keys :req-un [::foo ::bar] :opt-un [::baz]))
(p/json-schema ::thing)
=> {:type "object",
    :properties
    {"foo" {:type "string"},
     "bar" {:type "string"},
     "baz" {:type "integer", :format "int64"}},
    :required ["foo" "bar"]}

;; merged maps
(p/json-schema `(s/merge (s/keys :req-un [::foo]) (s/keys :req-un [::bar]) (s/keys :opt-un [::baz])))
=>  {:allOf
     [{:type "object",
       :properties {"foo" {:type "string"}},
       :required ["foo"]}
      {:type "object",
       :properties {"bar" {:type "string"}},
       :required ["bar"]}
      {:type "object",
       :properties {"baz" {:type "integer", :format "int64"}}}]}


;; multi-specs
(s/def ::tag #{:a :b :c :d})
(s/def ::example-key keyword?)
(s/def ::different-key keyword?)
(defmulti tagmm :tag)
(defmethod tagmm :a [_] (s/keys :req-un [::tag ::example-key]))
(defmethod tagmm :default [_] (s/keys :req-un [::tag ::different-key]))
(s/def ::ms (s/multi-spec tagmm :tag))

(p/json-schema ::ms)
=> {:oneOf
    [{:type "object",
      :properties {"tag" {:enum #{:c :b :d :a}}, "different-key" {:type "string"}}, 
      :required ["tag" "different-key"]}
     {:type "object",
      :properties {"tag" {:enum #{:c :b :d :a}}, "example-key" {:type "string"}},
      :required ["tag" "example-key"]}]}

;; arbitrary predicates 

(p/json-schema `(s/and number? (fn [x] (>= 10 x))))
=> {:allOf [{:type "number"} {:minimum 10, :type "number"}]}


;; metadata 

(-> (s/def ::animal string?)
    (p/with-description "An animal")
    (p/with-title "Animal")
    (p/with-pattern "[a-zA-Z]")
    (p/json-schema))
    
=> {:type "string", :title "Animal", :description "An animal", :pattern "[a-zA-Z]"}

;; overriding output, we make string? return something different
(p/json-schema ::animal {:idents {`string? {:type "string" :foo "bar"}}})
=>
{:type "string",
 :foo "bar",
 :title "Animal",
 :description "An animal",
 :pattern "[a-zA-Z]"}

;; also works for parameterized forms 
(p/json-schema `(s/coll-of any?) {:forms {`s/coll-of (fn [_coll-of-arg _opts] {:foo :bar})}})
=> {:foo :bar}

;; it understands alias chains
(s/def ::foo string?)
(s/def ::bar ::foo)
(s/def ::baz ::bar)
(p/json-schema ::baz)
=> {:type "string"}

;; also inherits all properties from chains
(p/with-pattern ::bar "[a-zA-Z]")
(p/json-schema ::baz)
{:type "string", :pattern "[a-zA-Z]"}

;; and allow overrides at any level
(with-pattern ::baz "[a-z]")
(p/json-schema ::baz)
=> {:type "string", :pattern "[a-z]"}
```

## Extensions

You can extend the way pact generates schemas via `json-schema` options or by registering schema generators

* `s-exp.pact/register-ident!`: registers a new generator for an `ident` (spec key or symbol)

```clj
(register-ident! `int? {:type "integer" :format "int64"})
```

* `s-exp.pact/register-form!`: registers a generator for a `form`, ex: `coll-of`
  parameterized spec forms (think, `int-in`, `nilable`, `coll-of` and so on)
  ex: how nilable is implemented
  
```clj
(p/register-form!
 `s/nilable
 (fn [[form] opts]
   {:oneOf [{:type "null"}
            (json-schema* form opts)]}))
```
           
* `s-exp.pact/register-pred!`: allows to set predicate conformer & schema
  generator for **arbitrary predicates** found in spec forms, ex: `(s/and  string? (fn [s] (str/includes? s something)))`

  It takes 2 arguments a spec key, that would be used to potentially conform
  (pattern match) a spec form onto bindings to use for a json-schema, and a
  function that will receive these arguments and return json-schema data
  matching the form.
  
There are also matching options you can pass to the `json-schema` function to
have overrides be applied per call (without being registered globally):

* `:idents` - map of idents -> values
* `:forms` - map of forms -> function of form args+opts
* `:preds` - map of pred conformer spec key -> generator
  function which will take the conformed values and options

## Caveats

* `s/keys` using `and`/`or` (ex: `(s/keys :req [(and (or ...)) ])) are just
  flattened for now, if that doesn't work for what you need, just override the
  generation for the concerned spec keys.

* `s/cat` generates an `array` type, if that doesn't work for what you need,
  just override the generation for the concerned spec keys.

## API 

see [API.md](API.md)

## Tests

`clj -X:test`

## License 

Copyright © 2024 Max Penet

Distributed under the Eclipse Public License version 1.0.
