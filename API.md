# Table of contents
-  [`s-exp.pact`](#s-exp.pact) 
    -  [`assoc-meta`](#s-exp.pact/assoc-meta) - Assoc <code>k</code>-><code>x</code> on metadata for <code>spec</code>.
    -  [`default-opts`](#s-exp.pact/default-opts)
    -  [`json-schema`](#s-exp.pact/json-schema) - Generate json-schema for <code>spec</code>.
    -  [`meta`](#s-exp.pact/meta) - Returns <code>spec</code> metadata.
    -  [`register-form!`](#s-exp.pact/register-form!) - Registers a <code>form</code> for generation.
    -  [`register-ident!`](#s-exp.pact/register-ident!) - Registers an <code>ident</code> for generation.
    -  [`register-pred!`](#s-exp.pact/register-pred!) - Sets <code>conformer</code> and <code>schema-fn</code> for predicate parser.
    -  [`registry`](#s-exp.pact/registry) - Returns registry.
    -  [`registry-ref`](#s-exp.pact/registry-ref)
    -  [`vary-meta`](#s-exp.pact/vary-meta) - Like <code>clojure.core/vary-meta but on spec </code>k` metadata.
    -  [`with-description`](#s-exp.pact/with-description) - Add <code>description</code> to spec.
    -  [`with-format`](#s-exp.pact/with-format) - Add <code>format</code> to spec.
    -  [`with-id`](#s-exp.pact/with-id) - Adds $id to spec.
    -  [`with-meta`](#s-exp.pact/with-meta) - Sets metadata for <code>spec</code>.
    -  [`with-pattern`](#s-exp.pact/with-pattern) - Add <code>pattern</code> to spec.
    -  [`with-title`](#s-exp.pact/with-title) - Add <code>title</code> to spec.

-----
# <a name="s-exp.pact">s-exp.pact</a>






## <a name="s-exp.pact/assoc-meta">`assoc-meta`</a><a name="s-exp.pact/assoc-meta"></a>
``` clojure

(assoc-meta spec k x)
```

Assoc `k`->`x` on metadata for `spec` 
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L77-L80">Source</a></sub></p>

## <a name="s-exp.pact/default-opts">`default-opts`</a><a name="s-exp.pact/default-opts"></a>



<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L18-L22">Source</a></sub></p>

## <a name="s-exp.pact/json-schema">`json-schema`</a><a name="s-exp.pact/json-schema"></a>
``` clojure

(json-schema k & {:as opts, :keys [property-key-fn strict gen-only-first-and-arg unknown-spec-default]})
```

Generate json-schema for `spec`.
  `opts` support:

  * `property-key-fn` - function that will convert the spec keys to json-schema
  keys - defaults to `name`

  * `strict` - whether to throw or not upon encountering unknown values for
  conversion - defaults to `true`

  * `gen-only-first-and-arg` - whether to only attempt generate the first
  predicate of `s/and` - defaults to `false`

  * `unknown-spec-default` - value to be used for unknown values for conversion
  - defaults to `nil`
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L108-L152">Source</a></sub></p>

## <a name="s-exp.pact/meta">`meta`</a><a name="s-exp.pact/meta"></a>
``` clojure

(meta spec)
```

Returns `spec` metadata
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L72-L75">Source</a></sub></p>

## <a name="s-exp.pact/register-form!">`register-form!`</a><a name="s-exp.pact/register-form!"></a>
``` clojure

(register-form! form f)
```

Registers a `form` for generation. Upon encountering that form (by matching its
  against first element or sequential symbolic spec values), [`json-schema`](#s-exp.pact/json-schema) will
  then call `f` against its arguments for generation
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L42-L48">Source</a></sub></p>

## <a name="s-exp.pact/register-ident!">`register-ident!`</a><a name="s-exp.pact/register-ident!"></a>
``` clojure

(register-ident! ident x)
```

Registers an `ident` for generation. Upon encountering a qualified
  symbol/keyword matching an `ident` in the registry it will return `x` as
  generated value
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L50-L56">Source</a></sub></p>

## <a name="s-exp.pact/register-pred!">`register-pred!`</a><a name="s-exp.pact/register-pred!"></a>
``` clojure

(register-pred! k schema-fn _opts)
(register-pred! k schema-fn)
```

Sets `conformer` and `schema-fn` for predicate parser.
  If a conformer matches, the bindings we get from the s/conform result will be
  passed to `schema-fn` in order to generate an appropriate json-schema value
  for the predicate.
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L303-L315">Source</a></sub></p>

## <a name="s-exp.pact/registry">`registry`</a><a name="s-exp.pact/registry"></a>
``` clojure

(registry)
(registry k)
```

Returns registry
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L35-L40">Source</a></sub></p>

## <a name="s-exp.pact/registry-ref">`registry-ref`</a><a name="s-exp.pact/registry-ref"></a>



<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L24-L28">Source</a></sub></p>

## <a name="s-exp.pact/vary-meta">`vary-meta`</a><a name="s-exp.pact/vary-meta"></a>
``` clojure

(vary-meta k f & args)
```

Like `clojure.core/vary-meta but on spec `k` metadata
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L58-L65">Source</a></sub></p>

## <a name="s-exp.pact/with-description">`with-description`</a><a name="s-exp.pact/with-description"></a>
``` clojure

(with-description k description)
```

Add `description` to spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L92-L95">Source</a></sub></p>

## <a name="s-exp.pact/with-format">`with-format`</a><a name="s-exp.pact/with-format"></a>
``` clojure

(with-format k fmt)
```

Add `format` to spec.
  See https://json-schema.org/understanding-json-schema/reference/string#built-in-formats
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L97-L101">Source</a></sub></p>

## <a name="s-exp.pact/with-id">`with-id`</a><a name="s-exp.pact/with-id"></a>
``` clojure

(with-id spec id)
```

Adds $id to spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L82-L85">Source</a></sub></p>

## <a name="s-exp.pact/with-meta">`with-meta`</a><a name="s-exp.pact/with-meta"></a>
``` clojure

(with-meta spec x)
```

Sets metadata for `spec` 
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L67-L70">Source</a></sub></p>

## <a name="s-exp.pact/with-pattern">`with-pattern`</a><a name="s-exp.pact/with-pattern"></a>
``` clojure

(with-pattern k p)
```

Add `pattern` to spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L103-L106">Source</a></sub></p>

## <a name="s-exp.pact/with-title">`with-title`</a><a name="s-exp.pact/with-title"></a>
``` clojure

(with-title k title)
```

Add `title` to spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact.clj#L87-L90">Source</a></sub></p>
