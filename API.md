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
-  [`s-exp.pact.impl`](#s-exp.pact.impl) 
    -  [`abbrev`](#s-exp.pact.impl/abbrev)
    -  [`accept-keyword`](#s-exp.pact.impl/accept-keyword)
    -  [`accept-set`](#s-exp.pact.impl/accept-set)
    -  [`accept-symbol`](#s-exp.pact.impl/accept-symbol)
    -  [`accept-symbol-call`](#s-exp.pact.impl/accept-symbol-call)
    -  [`array-schema`](#s-exp.pact.impl/array-schema)
    -  [`find-description`](#s-exp.pact.impl/find-description) - Find first <code>description</code> value in spec hierarchy for spec.
    -  [`find-format`](#s-exp.pact.impl/find-format) - Find first <code>format</code> value in spec hierarchy for spec.
    -  [`find-id`](#s-exp.pact.impl/find-id) - Find first <code>$id</code> value in spec hierarchy for spec.
    -  [`find-key`](#s-exp.pact.impl/find-key) - Find first <code>prop</code> value in spec hierarchy for spec.
    -  [`find-pattern`](#s-exp.pact.impl/find-pattern) - Find first <code>pattern</code> value in spec hierarchy for spec.
    -  [`find-title`](#s-exp.pact.impl/find-title) - Find first <code>title</code> value in spec hierarchy for spec.
    -  [`parent-spec`](#s-exp.pact.impl/parent-spec) - Look up for the parent coercer using the spec hierarchy.
    -  [`pred-conformer`](#s-exp.pact.impl/pred-conformer)
    -  [`registry-form`](#s-exp.pact.impl/registry-form) - Returns registry form function for key <code>k</code>.
    -  [`registry-ident`](#s-exp.pact.impl/registry-ident) - Returns registry ident value for key <code>k</code>.
    -  [`registry-meta`](#s-exp.pact.impl/registry-meta) - Returns metadata registry or metadata for spec <code>k</code>.
    -  [`resolve-schema`](#s-exp.pact.impl/resolve-schema) - Find first schema generator in spec hierarchy.
    -  [`spec-chain`](#s-exp.pact.impl/spec-chain) - Determine the main spec root from a spec form.
    -  [`spec-form`](#s-exp.pact.impl/spec-form) - Return the spec form or nil.
    -  [`spec-root`](#s-exp.pact.impl/spec-root) - Determine the main spec root from a spec form.
    -  [`string-schema`](#s-exp.pact.impl/string-schema)
    -  [`strip-core`](#s-exp.pact.impl/strip-core)

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

-----
# <a name="s-exp.pact.impl">s-exp.pact.impl</a>






## <a name="s-exp.pact.impl/abbrev">`abbrev`</a><a name="s-exp.pact.impl/abbrev"></a>
``` clojure

(abbrev form)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L94-L111">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-keyword">`accept-keyword`</a><a name="s-exp.pact.impl/accept-keyword"></a>
``` clojure

(accept-keyword x)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L124-L126">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-set">`accept-set`</a><a name="s-exp.pact.impl/accept-set"></a>
``` clojure

(accept-set x)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L132-L134">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-symbol">`accept-symbol`</a><a name="s-exp.pact.impl/accept-symbol"></a>
``` clojure

(accept-symbol x)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L128-L130">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-symbol-call">`accept-symbol-call`</a><a name="s-exp.pact.impl/accept-symbol-call"></a>
``` clojure

(accept-symbol-call spec)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L136-L139">Source</a></sub></p>

## <a name="s-exp.pact.impl/array-schema">`array-schema`</a><a name="s-exp.pact.impl/array-schema"></a>
``` clojure

(array-schema)
(array-schema opts)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L181-L184">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-description">`find-description`</a><a name="s-exp.pact.impl/find-description"></a>




Find first `description` value in spec hierarchy for spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L70-L72">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-format">`find-format`</a><a name="s-exp.pact.impl/find-format"></a>




Find first `format` value in spec hierarchy for spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L78-L80">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-id">`find-id`</a><a name="s-exp.pact.impl/find-id"></a>




Find first `$id` value in spec hierarchy for spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L62-L64">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-key">`find-key`</a><a name="s-exp.pact.impl/find-key"></a>
``` clojure

(find-key prop)
```

Find first `prop` value in spec hierarchy for spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L50-L60">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-pattern">`find-pattern`</a><a name="s-exp.pact.impl/find-pattern"></a>




Find first `pattern` value in spec hierarchy for spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L82-L84">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-title">`find-title`</a><a name="s-exp.pact.impl/find-title"></a>




Find first `title` value in spec hierarchy for spec
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L66-L68">Source</a></sub></p>

## <a name="s-exp.pact.impl/parent-spec">`parent-spec`</a><a name="s-exp.pact.impl/parent-spec"></a>
``` clojure

(parent-spec k)
```

Look up for the parent coercer using the spec hierarchy.
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L157-L161">Source</a></sub></p>

## <a name="s-exp.pact.impl/pred-conformer">`pred-conformer`</a><a name="s-exp.pact.impl/pred-conformer"></a>
``` clojure

(pred-conformer pred {:as opts, :s-exp.pact.json-schema/keys [preds]})
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L113-L120">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-form">`registry-form`</a><a name="s-exp.pact.impl/registry-form"></a>
``` clojure

(registry-form registry-val k)
```

Returns registry form function for key `k`
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L15-L18">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-ident">`registry-ident`</a><a name="s-exp.pact.impl/registry-ident"></a>
``` clojure

(registry-ident registry-val k)
```

Returns registry ident value for key `k`
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L20-L23">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-meta">`registry-meta`</a><a name="s-exp.pact.impl/registry-meta"></a>
``` clojure

(registry-meta registry-val)
(registry-meta registry-val k)
```

Returns metadata registry or metadata for spec `k`
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L9-L13">Source</a></sub></p>

## <a name="s-exp.pact.impl/resolve-schema">`resolve-schema`</a><a name="s-exp.pact.impl/resolve-schema"></a>
``` clojure

(resolve-schema registry-val spec-chain {:as opts, :keys [idents forms preds]})
```

Find first schema generator in spec hierarchy
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L25-L48">Source</a></sub></p>

## <a name="s-exp.pact.impl/spec-chain">`spec-chain`</a><a name="s-exp.pact.impl/spec-chain"></a>
``` clojure

(spec-chain spec)
```

Determine the main spec root from a spec form.
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L163-L172">Source</a></sub></p>

## <a name="s-exp.pact.impl/spec-form">`spec-form`</a><a name="s-exp.pact.impl/spec-form"></a>
``` clojure

(spec-form spec)
```

Return the spec form or nil.
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L141-L144">Source</a></sub></p>

## <a name="s-exp.pact.impl/spec-root">`spec-root`</a><a name="s-exp.pact.impl/spec-root"></a>
``` clojure

(spec-root spec)
```

Determine the main spec root from a spec form.
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L146-L155">Source</a></sub></p>

## <a name="s-exp.pact.impl/string-schema">`string-schema`</a><a name="s-exp.pact.impl/string-schema"></a>
``` clojure

(string-schema)
(string-schema opts)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L176-L179">Source</a></sub></p>

## <a name="s-exp.pact.impl/strip-core">`strip-core`</a><a name="s-exp.pact.impl/strip-core"></a>
``` clojure

(strip-core sym)
```
<p><sub><a href="https://github.com/exoscale/canary/blob/master/src/s_exp/pact/impl.clj#L88-L92">Source</a></sub></p>
