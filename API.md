# Table of contents
-  [`s-exp.pact`](#s-exp.pact) 
    -  [`assoc-meta`](#s-exp.pact/assoc-meta) - Assoc <code>k</code>-><code>x</code> on metadata for <code>spec</code>.
    -  [`default-opts`](#s-exp.pact/default-opts)
    -  [`derive`](#s-exp.pact/derive) - Like clojure.core/derive but scoped on our <code>hierarchy</code>.
    -  [`find-description`](#s-exp.pact/find-description) - Find first <code>description</code> value in spec hierarchy for spec.
    -  [`find-format`](#s-exp.pact/find-format) - Find first <code>format</code> value in spec hierarchy for spec.
    -  [`find-id`](#s-exp.pact/find-id) - Find first <code>$id</code> value in spec hierarchy for spec.
    -  [`find-pattern`](#s-exp.pact/find-pattern) - Find first <code>pattern</code> value in spec hierarchy for spec.
    -  [`find-schema`](#s-exp.pact/find-schema) - Find first <code>schema</code> value in spec hierarchy for spec.
    -  [`find-title`](#s-exp.pact/find-title) - Find first <code>title</code> value in spec hierarchy for spec.
    -  [`hierarchy`](#s-exp.pact/hierarchy) - Internal hierarchy used by <code>schema</code>.
    -  [`json-schema`](#s-exp.pact/json-schema) - Generate json-schema for <code>spec</code>.
    -  [`json-schema*`](#s-exp.pact/json-schema*) - Like <code>json-schema</code>, but doesn't do any caching.
    -  [`meta`](#s-exp.pact/meta)
    -  [`registry`](#s-exp.pact/registry)
    -  [`registry-ref`](#s-exp.pact/registry-ref)
    -  [`schema`](#s-exp.pact/schema)
    -  [`set-pred-conformer!`](#s-exp.pact/set-pred-conformer!)
    -  [`set-pred-schema!`](#s-exp.pact/set-pred-schema!)
    -  [`vary-meta`](#s-exp.pact/vary-meta) - Like <code>clojure.core/vary-meta but on spec </code>k` metadata.
    -  [`with-description`](#s-exp.pact/with-description) - Add <code>description</code> to spec.
    -  [`with-format`](#s-exp.pact/with-format) - Add <code>format</code> to spec.
    -  [`with-id`](#s-exp.pact/with-id) - Adds $id to spec.
    -  [`with-pattern`](#s-exp.pact/with-pattern) - Add <code>pattern</code> to spec.
    -  [`with-title`](#s-exp.pact/with-title) - Add <code>title</code> to spec.
-  [`s-exp.pact.impl`](#s-exp.pact.impl) 
    -  [`abbrev`](#s-exp.pact.impl/abbrev)
    -  [`pred-conformer`](#s-exp.pact.impl/pred-conformer)
    -  [`pred-schema`](#s-exp.pact.impl/pred-schema)
    -  [`registry-lookup`](#s-exp.pact.impl/registry-lookup)
    -  [`strip-core`](#s-exp.pact.impl/strip-core)
-  [`s-exp.pact.inspect`](#s-exp.pact.inspect) 
    -  [`accept-keyword`](#s-exp.pact.inspect/accept-keyword)
    -  [`accept-set`](#s-exp.pact.inspect/accept-set)
    -  [`accept-symbol`](#s-exp.pact.inspect/accept-symbol)
    -  [`accept-symbol-call`](#s-exp.pact.inspect/accept-symbol-call)
    -  [`parent-spec`](#s-exp.pact.inspect/parent-spec) - Look up for the parent coercer using the spec hierarchy.
    -  [`spec-ancestors`](#s-exp.pact.inspect/spec-ancestors)
    -  [`spec-form`](#s-exp.pact.inspect/spec-form) - Return the spec form or nil.
    -  [`spec-root`](#s-exp.pact.inspect/spec-root) - Determine the main spec root from a spec form.

-----
# <a name="s-exp.pact">s-exp.pact</a>






## <a name="s-exp.pact/assoc-meta">`assoc-meta`</a><a name="s-exp.pact/assoc-meta"></a>
``` clojure

(assoc-meta spec k x)
```

Assoc `k`->`x` on metadata for `spec` 
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L37-L40">Source</a></sub></p>

## <a name="s-exp.pact/default-opts">`default-opts`</a><a name="s-exp.pact/default-opts"></a>



<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L8-L12">Source</a></sub></p>

## <a name="s-exp.pact/derive">`derive`</a><a name="s-exp.pact/derive"></a>
``` clojure

(derive tag parent)
```

Like clojure.core/derive but scoped on our [`hierarchy`](#s-exp.pact/hierarchy)
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L139-L143">Source</a></sub></p>

## <a name="s-exp.pact/find-description">`find-description`</a><a name="s-exp.pact/find-description"></a>
``` clojure

(find-description k)
```

Find first `description` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L77-L80">Source</a></sub></p>

## <a name="s-exp.pact/find-format">`find-format`</a><a name="s-exp.pact/find-format"></a>
``` clojure

(find-format k)
```

Find first `format` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L87-L90">Source</a></sub></p>

## <a name="s-exp.pact/find-id">`find-id`</a><a name="s-exp.pact/find-id"></a>
``` clojure

(find-id k)
```

Find first `$id` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L67-L70">Source</a></sub></p>

## <a name="s-exp.pact/find-pattern">`find-pattern`</a><a name="s-exp.pact/find-pattern"></a>
``` clojure

(find-pattern k)
```

Find first `pattern` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L92-L95">Source</a></sub></p>

## <a name="s-exp.pact/find-schema">`find-schema`</a><a name="s-exp.pact/find-schema"></a>
``` clojure

(find-schema k)
```

Find first [`schema`](#s-exp.pact/schema) value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L82-L85">Source</a></sub></p>

## <a name="s-exp.pact/find-title">`find-title`</a><a name="s-exp.pact/find-title"></a>
``` clojure

(find-title k)
```

Find first `title` value in spec hierarchy for spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L72-L75">Source</a></sub></p>

## <a name="s-exp.pact/hierarchy">`hierarchy`</a><a name="s-exp.pact/hierarchy"></a>




Internal hierarchy used by [`schema`](#s-exp.pact/schema)
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L135-L137">Source</a></sub></p>

## <a name="s-exp.pact/json-schema">`json-schema`</a><a name="s-exp.pact/json-schema"></a>
``` clojure

(json-schema spec & {:as opts})
```

Generate json-schema for `spec`
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L406-L412">Source</a></sub></p>

## <a name="s-exp.pact/json-schema*">`json-schema*`</a><a name="s-exp.pact/json-schema*"></a>
``` clojure

(json-schema* k opts)
```

Like [`json-schema`](#s-exp.pact/json-schema), but doesn't do any caching
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L109-L133">Source</a></sub></p>

## <a name="s-exp.pact/meta">`meta`</a><a name="s-exp.pact/meta"></a>
``` clojure

(meta)
(meta k)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L24-L27">Source</a></sub></p>

## <a name="s-exp.pact/registry">`registry`</a><a name="s-exp.pact/registry"></a>
``` clojure

(registry)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L20-L22">Source</a></sub></p>

## <a name="s-exp.pact/registry-ref">`registry-ref`</a><a name="s-exp.pact/registry-ref"></a>



<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L14-L18">Source</a></sub></p>

## <a name="s-exp.pact/schema">`schema`</a><a name="s-exp.pact/schema"></a>



<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L145-L151">Source</a></sub></p>

## <a name="s-exp.pact/set-pred-conformer!">`set-pred-conformer!`</a><a name="s-exp.pact/set-pred-conformer!"></a>
``` clojure

(set-pred-conformer! k schema-fn _opts)
(set-pred-conformer! k schema-fn)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L99-L107">Source</a></sub></p>

## <a name="s-exp.pact/set-pred-schema!">`set-pred-schema!`</a><a name="s-exp.pact/set-pred-schema!"></a>
``` clojure

(set-pred-schema! spec-key schema-fn)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L333-L335">Source</a></sub></p>

## <a name="s-exp.pact/vary-meta">`vary-meta`</a><a name="s-exp.pact/vary-meta"></a>
``` clojure

(vary-meta k f & args)
```

Like `clojure.core/vary-meta but on spec `k` metadata
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L29-L35">Source</a></sub></p>

## <a name="s-exp.pact/with-description">`with-description`</a><a name="s-exp.pact/with-description"></a>
``` clojure

(with-description k description)
```

Add `description` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L52-L55">Source</a></sub></p>

## <a name="s-exp.pact/with-format">`with-format`</a><a name="s-exp.pact/with-format"></a>
``` clojure

(with-format k fmt)
```

Add `format` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L57-L60">Source</a></sub></p>

## <a name="s-exp.pact/with-id">`with-id`</a><a name="s-exp.pact/with-id"></a>
``` clojure

(with-id spec id)
```

Adds $id to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L42-L45">Source</a></sub></p>

## <a name="s-exp.pact/with-pattern">`with-pattern`</a><a name="s-exp.pact/with-pattern"></a>
``` clojure

(with-pattern k p)
```

Add `pattern` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L62-L65">Source</a></sub></p>

## <a name="s-exp.pact/with-title">`with-title`</a><a name="s-exp.pact/with-title"></a>
``` clojure

(with-title k title)
```

Add `title` to spec
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact.clj#L47-L50">Source</a></sub></p>

-----
# <a name="s-exp.pact.impl">s-exp.pact.impl</a>






## <a name="s-exp.pact.impl/abbrev">`abbrev`</a><a name="s-exp.pact.impl/abbrev"></a>
``` clojure

(abbrev form)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L14-L31">Source</a></sub></p>

## <a name="s-exp.pact.impl/pred-conformer">`pred-conformer`</a><a name="s-exp.pact.impl/pred-conformer"></a>
``` clojure

(pred-conformer registry-val conformers pred opts)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L39-L46">Source</a></sub></p>

## <a name="s-exp.pact.impl/pred-schema">`pred-schema`</a><a name="s-exp.pact.impl/pred-schema"></a>
``` clojure

(pred-schema registry-val k match opts)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L33-L37">Source</a></sub></p>

## <a name="s-exp.pact.impl/registry-lookup">`registry-lookup`</a><a name="s-exp.pact.impl/registry-lookup"></a>
``` clojure

(registry-lookup registry k f)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L48-L54">Source</a></sub></p>

## <a name="s-exp.pact.impl/strip-core">`strip-core`</a><a name="s-exp.pact.impl/strip-core"></a>
``` clojure

(strip-core sym)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/impl.clj#L8-L12">Source</a></sub></p>

-----
# <a name="s-exp.pact.inspect">s-exp.pact.inspect</a>






## <a name="s-exp.pact.inspect/accept-keyword">`accept-keyword`</a><a name="s-exp.pact.inspect/accept-keyword"></a>
``` clojure

(accept-keyword x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L4-L6">Source</a></sub></p>

## <a name="s-exp.pact.inspect/accept-set">`accept-set`</a><a name="s-exp.pact.inspect/accept-set"></a>
``` clojure

(accept-set x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L12-L14">Source</a></sub></p>

## <a name="s-exp.pact.inspect/accept-symbol">`accept-symbol`</a><a name="s-exp.pact.inspect/accept-symbol"></a>
``` clojure

(accept-symbol x)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L8-L10">Source</a></sub></p>

## <a name="s-exp.pact.inspect/accept-symbol-call">`accept-symbol-call`</a><a name="s-exp.pact.inspect/accept-symbol-call"></a>
``` clojure

(accept-symbol-call spec)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L16-L19">Source</a></sub></p>

## <a name="s-exp.pact.inspect/parent-spec">`parent-spec`</a><a name="s-exp.pact.inspect/parent-spec"></a>
``` clojure

(parent-spec k)
```

Look up for the parent coercer using the spec hierarchy.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L37-L41">Source</a></sub></p>

## <a name="s-exp.pact.inspect/spec-ancestors">`spec-ancestors`</a><a name="s-exp.pact.inspect/spec-ancestors"></a>
``` clojure

(spec-ancestors k)
```
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L43-L49">Source</a></sub></p>

## <a name="s-exp.pact.inspect/spec-form">`spec-form`</a><a name="s-exp.pact.inspect/spec-form"></a>
``` clojure

(spec-form spec)
```

Return the spec form or nil.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L21-L24">Source</a></sub></p>

## <a name="s-exp.pact.inspect/spec-root">`spec-root`</a><a name="s-exp.pact.inspect/spec-root"></a>
``` clojure

(spec-root spec)
```

Determine the main spec root from a spec form.
<p><sub><a href="https://github.com/mpenet/pact/blob/main/src/s_exp/pact/inspect.clj#L26-L35">Source</a></sub></p>
