# pact

/!\ WIP

Generate json-schema / OpenAPI 3.1+ from clojure specs.

# Rationale

Specs allow us to define contracts, why not re-using these to **generate
json-schema**. Spec forms are just "data", should be easy right?

Well, there are a few challenges:
* specs can be defined as arbitrary predicates
* specs can also be aliases to other specs
* specs can be the result of composition of other specs via s/and s/merge
  s/multi-spec s/keys etc
* specs have no metadata, that makes adding features json-schema has but spec
  hasn't a bit complicated

How `pact` attempts to handle these:

* **arbitrary predicates**: Predicate forms parsing is done using spec, with
  conform, that's all you need to know to do it yourself. We conform predicate
  forms against a set of conformers we hold internally (that you can extend
  yourself), so that you can destructure from the conform call (ex grab argument
  values, or anything really) in order to then generate the appropriate openapi
  data via a supplied function. `pact` comes with a number of useful predicate
  parsers that allow to generate correct schemas for common cases (numercic
  comparaisons, length bounds and so on).
  
* **spec aliases chains**: we ensure that alias chains are understood and walk them
  up trying to find the first spec key that will allow json-schema
  generation. For instance if you have a spec ::foo that is an alias to ::bar
  that is itself an alias to ::baz that is a `string?`, trying to generate
  json-schema for ::foo will check them in order until it finds enough
  information to do so (from ::baz definition).
  
* **spec forms/composition of specs**: schemas can be inferred for all
  clojure.spec forms (s/and & co). In the cases where we cannot infer the schema
  we provide ways for you to specify what to do. We also provide ways to extend
  what we generate out of the box.
  
* **metadata**: we have a few helpers that allow you to specify/override
  `description`, `format`, `pattern` on top of existing specs, that will later
  show up in the json-schema for these. They also understand spec aliases and
  pick up the first walking back the spec alias chain.
  
By default pact is **strict**, it will throw at generation time if it cannot
infer the json-schema for a spec, but it will allow you to specify the missing
bits.
It can also function in non strict mode where unknowns generate
`{:type"object"}`. You can also tune the interpretation of some forms to be
less strict, for instance having only the first component of a `s/and` to be
taken into account and a few others like this. But by default we try to cover
the full spec.

`pact` also provides an **openapi generator**, given that openapi 3.1.x+ is
json-schema compatible it's fairly easy to customize. It mostly sugars the
creation of paths, component.schemas and few other things, the rest is left to
the user as it generate just clojure maps (not json).

## Examples

wip

## Extensions

* `s-exp.pact/schema` : multimethod that controls generation of json schema for
  a spec form

* `s-exp.pact/set-pred-schema!`: allows to set predicate conformer & shema
  generator for arbitrary predicates found in spec forms.

## Caveats

* `s/keys` with `and`/`or` are just flattened for now, if that doesn't work for
  what you need, just override the generation for the concerned spec keys.

* `s/cat` generates an `array` type, if that doesn't work for what you need,
  just override the generation for the concerned spec keys.

## API 

see [API.md](API.md)

## Tests

`clj -X:test`


## Todo 

- [ ] tests
- [ ] ci
- [ ] cljs support

## License 

Copyright © 2023 Max Penet

Distributed under the Eclipse Public License version 1.0.
