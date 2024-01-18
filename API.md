# Table of contents
-  [`s-exp.pact`](#s-exp.pact) 
    -  [`assoc-meta`](#s-exp.pact/assoc-meta) - Assoc <code>k</code>-><code>x</code> on metadata for <code>spec</code>.
    -  [`default-opts`](#s-exp.pact/default-opts)
    -  [`json-schema`](#s-exp.pact/json-schema) - Generate json-schema for <code>spec</code>.
    -  [`json-schema*`](#s-exp.pact/json-schema*)
    -  [`register-form!`](#s-exp.pact/register-form!)
    -  [`register-ident!`](#s-exp.pact/register-ident!)
    -  [`register-pred!`](#s-exp.pact/register-pred!) - Sets <code>conformer</code> and <code>schema-fn</code> for predicate parser.
    -  [`registry`](#s-exp.pact/registry) - Returns registry.
    -  [`registry-ref`](#s-exp.pact/registry-ref)
    -  [`vary-meta`](#s-exp.pact/vary-meta) - Like <code>clojure.core/vary-meta but on spec </code>k` metadata.
    -  [`with-description`](#s-exp.pact/with-description) - Add <code>description</code> to spec.
    -  [`with-format`](#s-exp.pact/with-format) - Add <code>format</code> to spec.
    -  [`with-id`](#s-exp.pact/with-id) - Adds $id to spec.
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
    -  [`registry-form`](#s-exp.pact.impl/registry-form)
    -  [`registry-ident`](#s-exp.pact.impl/registry-ident)
    -  [`registry-meta`](#s-exp.pact.impl/registry-meta) - Returns metadata registry or metadata for spec <code>k</code>.
    -  [`resolve-schema`](#s-exp.pact.impl/resolve-schema)
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
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L46-L49">Source</a></sub></p>

## <a name="s-exp.pact/default-opts">`default-opts`</a><a name="s-exp.pact/default-opts"></a>



<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L7-L11">Source</a></sub></p>

## <a name="s-exp.pact/json-schema">`json-schema`</a><a name="s-exp.pact/json-schema"></a>
``` clojure

(json-schema spec & {:as opts})
```

Generate json-schema for `spec`
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L325-L331">Source</a></sub></p>

## <a name="s-exp.pact/json-schema*">`json-schema*`</a><a name="s-exp.pact/json-schema*"></a>
``` clojure

(json-schema* k opts)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L76-L101">Source</a></sub></p>

## <a name="s-exp.pact/register-form!">`register-form!`</a><a name="s-exp.pact/register-form!"></a>
``` clojure

(register-form! form f)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L27-L30">Source</a></sub></p>

## <a name="s-exp.pact/register-ident!">`register-ident!`</a><a name="s-exp.pact/register-ident!"></a>
``` clojure

(register-ident! ident x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L32-L35">Source</a></sub></p>

## <a name="s-exp.pact/register-pred!">`register-pred!`</a><a name="s-exp.pact/register-pred!"></a>
``` clojure

(register-pred! k schema-fn _opts)
(register-pred! k schema-fn)
```

Sets `conformer` and `schema-fn` for predicate parser.
  If a conformer matches, the bindings we get from the s/conform result will be
  passed to `schema-fn` in order to generate an appropriate json-schema value
  for the predicate.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L252-L264">Source</a></sub></p>

## <a name="s-exp.pact/registry">`registry`</a><a name="s-exp.pact/registry"></a>
``` clojure

(registry)
(registry k)
```

Returns registry
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L20-L25">Source</a></sub></p>

## <a name="s-exp.pact/registry-ref">`registry-ref`</a><a name="s-exp.pact/registry-ref"></a>



<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L13-L18">Source</a></sub></p>

## <a name="s-exp.pact/vary-meta">`vary-meta`</a><a name="s-exp.pact/vary-meta"></a>
``` clojure

(vary-meta k f & args)
```

Like `clojure.core/vary-meta but on spec `k` metadata
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L37-L44">Source</a></sub></p>

## <a name="s-exp.pact/with-description">`with-description`</a><a name="s-exp.pact/with-description"></a>
``` clojure

(with-description k description)
```

Add `description` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L61-L64">Source</a></sub></p>

## <a name="s-exp.pact/with-format">`with-format`</a><a name="s-exp.pact/with-format"></a>
``` clojure

(with-format k fmt)
```

Add `format` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L66-L69">Source</a></sub></p>

## <a name="s-exp.pact/with-id">`with-id`</a><a name="s-exp.pact/with-id"></a>
``` clojure

(with-id spec id)
```

Adds $id to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L51-L54">Source</a></sub></p>

## <a name="s-exp.pact/with-pattern">`with-pattern`</a><a name="s-exp.pact/with-pattern"></a>
``` clojure

(with-pattern k p)
```

Add `pattern` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L71-L74">Source</a></sub></p>

## <a name="s-exp.pact/with-title">`with-title`</a><a name="s-exp.pact/with-title"></a>
``` clojure

(with-title k title)
```

Add `title` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L56-L59">Source</a></sub></p>

-----
# <a name="s-exp.pact.impl">s-exp.pact.impl</a>






## <a name="s-exp.pact.impl/abbrev">`abbrev`</a><a name="s-exp.pact.impl/abbrev"></a>
``` clojure

(abbrev form)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L93-L110">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-keyword">`accept-keyword`</a><a name="s-exp.pact.impl/accept-keyword"></a>
``` clojure

(accept-keyword x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L123-L125">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-set">`accept-set`</a><a name="s-exp.pact.impl/accept-set"></a>
``` clojure

(accept-set x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L131-L133">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-symbol">`accept-symbol`</a><a name="s-exp.pact.impl/accept-symbol"></a>
``` clojure

(accept-symbol x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L127-L129">Source</a></sub></p>

## <a name="s-exp.pact.impl/accept-symbol-call">`accept-symbol-call`</a><a name="s-exp.pact.impl/accept-symbol-call"></a>
``` clojure

(accept-symbol-call spec)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L135-L138">Source</a></sub></p>

## <a name="s-exp.pact.impl/array-schema">`array-schema`</a><a name="s-exp.pact.impl/array-schema"></a>
``` clojure

(array-schema)
(array-schema opts)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L181-L184">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-description">`find-description`</a><a name="s-exp.pact.impl/find-description"></a>




Find first `description` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L69-L71">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-format">`find-format`</a><a name="s-exp.pact.impl/find-format"></a>




Find first `format` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L77-L79">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-id">`find-id`</a><a name="s-exp.pact.impl/find-id"></a>




Find first `$id` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L61-L63">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-key">`find-key`</a><a name="s-exp.pact.impl/find-key"></a>
``` clojure

(find-key prop)
```

Find first `prop` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L49-L59">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-pattern">`find-pattern`</a><a name="s-exp.pact.impl/find-pattern"></a>




Find first `pattern` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L81-L83">Source</a></sub></p>

## <a name="s-exp.pact.impl/find-title">`find-title`</a><a name="s-exp.pact.impl/find-title"></a>




Find first `title` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L65-L67">Source</a></sub></p>

## <a name="s-exp.pact.impl/parent-spec">`parent-spec`</a><a name="s-exp.pact.impl/parent-spec"></a>
``` clojure

(parent-spec k)
```

Look up for the parent coercer using the spec hierarchy.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L156-L160">Source</a></sub></p>

## <a name="s-exp.pact.impl/pred-conformer">`pred-conformer`</a><a name="s-exp.pact.impl/pred-conformer"></a>
``` clojure

(pred-conformer pred {:as opts, :s-exp.pact.json-schema/keys [preds]})
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L112-L119">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-form">`registry-form`</a><a name="s-exp.pact.impl/registry-form"></a>
``` clojure

(registry-form registry-val k)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L15-L17">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-ident">`registry-ident`</a><a name="s-exp.pact.impl/registry-ident"></a>
``` clojure

(registry-ident registry-val k)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L19-L21">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-meta">`registry-meta`</a><a name="s-exp.pact.impl/registry-meta"></a>
``` clojure

(registry-meta registry-val)
(registry-meta registry-val k)
```

Returns metadata registry or metadata for spec `k`
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L9-L13">Source</a></sub></p>

## <a name="s-exp.pact.impl/resolve-schema">`resolve-schema`</a><a name="s-exp.pact.impl/resolve-schema"></a>
``` clojure

(resolve-schema registry-val spec-chain {:as opts, :s-exp.pact.json-schema/keys [idents forms preds]})
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L23-L47">Source</a></sub></p>

## <a name="s-exp.pact.impl/spec-chain">`spec-chain`</a><a name="s-exp.pact.impl/spec-chain"></a>
``` clojure

(spec-chain spec)
```

Determine the main spec root from a spec form.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L162-L171">Source</a></sub></p>

## <a name="s-exp.pact.impl/spec-form">`spec-form`</a><a name="s-exp.pact.impl/spec-form"></a>
``` clojure

(spec-form spec)
```

Return the spec form or nil.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L140-L143">Source</a></sub></p>

## <a name="s-exp.pact.impl/spec-root">`spec-root`</a><a name="s-exp.pact.impl/spec-root"></a>
``` clojure

(spec-root spec)
```

Determine the main spec root from a spec form.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L145-L154">Source</a></sub></p>

## <a name="s-exp.pact.impl/string-schema">`string-schema`</a><a name="s-exp.pact.impl/string-schema"></a>
``` clojure

(string-schema)
(string-schema opts)
```


<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L175-L179">Source</a></sub></p>

## <a name="s-exp.pact.impl/strip-core">`strip-core`</a><a name="s-exp.pact.impl/strip-core"></a>
``` clojure

(strip-core sym)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L87-L91">Source</a></sub></p>
