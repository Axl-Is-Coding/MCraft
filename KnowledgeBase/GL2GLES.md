# OpenGL to OpenGL ES 3.2 Migration Cheat Sheet

A definitive reference guide for porting modern desktop OpenGL applications to **OpenGL ES 3.2 (GLES 3.2)**. This document highlights what is removed, what requires substitution, and critical architectural changes for mobile and embedded platforms.

---

## 1. Quick Reference: Core Differences


| Feature / Capability | Desktop OpenGL (4.6) | OpenGL ES 3.2 | Action Required for Migration |
| :--- | :--- | :--- | :--- |
| **Fixed-Function Pipeline** | Supported (Legacy) | ❌ **Completely Removed** | Rewrite using shaders, VBOs, and VAOs. |
| **Default GLSL Precision** | Implicit High Precision | ⚠️ **Mandatory in Fragment** | Define `lowp`, `mediump`, or `highp` explicitly. |
| **Geometry & Tessellation** | Core | Core (since 3.2) | Ensure hardware supports ES 3.2 features. |
| **Compute Shaders** | Core | Core (since 3.1) | Minor layout qualifier adjustments needed. |
| **Texture 1D / Rectangle** | Core | ❌ **Unsupported** | Emulate using `GL_TEXTURE_2D` or arrays. |
| **Polygon Modes** | `glPolygonMode()` | ❌ **Unsupported** | Emulate wireframes with `GL_LINES` shaders. |
| **Double Precision (`double`)**| Core | ❌ **Unsupported** | Convert all 64-bit data to 32-bit `float`. |

---

## 2. API Level Removals & Replacements

### ❌ Immediate Mode & Fixed Matrices
Legacy operations do not exist in OpenGL ES. Matrix calculations must happen on the CPU (using libraries like GLM) and be sent to the GPU as uniform matrices.

*   **Delete:** `glBegin()`, `glEnd()`, `glVertex3f()`, `glColor4f()`, `glTexCoord2f()`
*   **Delete:** `glMatrixMode()`, `glLoadIdentity()`, `glPushMatrix()`, `glPopMatrix()`, `glRotate()`, `glTranslate()`, `glScale()`
*   **Fix:** Generate Vertex Buffer Objects (VBOs) and bind them to generic vertex attributes via `glVertexAttribPointer`.

### ❌ Primitive Types
*   **Delete:** `GL_QUADS`, `GL_QUAD_STRIP`, `GL_POLYGON`
*   **Replace with:** `GL_TRIANGLES`, `GL_TRIANGLE_STRIP`, or `GL_TRIANGLE_FAN`. 
*   *Tip:* Split your quads into two triangles using index buffers (`GL_ELEMENT_ARRAY_BUFFER`).

### ❌ State & Query Removals
*   **Wireframe Mode:** `glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)` is missing. To draw wireframes, you must manually change your draw calls to primitive type `GL_LINES`.
*   **Line & Point Sizes:** `glLineWidth()` is strictly limited (often only supporting `1.0`). `glPointSize()` is only usable inside vertex shaders via `gl_PointSize`.

---

## 3. Vertex Data & Buffer Mapping

GLES 3.2 natively supports **Vertex Array Objects (VAOs)** and **Vertex Buffer Objects (VBOs)** identically to Core Desktop OpenGL, with a few data type restrictions.

### Data Types
*   **Do not use** `GL_DOUBLE`. GLES 3.2 mobile hardware is optimized for 32-bit floating-point and lower. Use `GL_FLOAT`.
*   For packed data, use `GL_HALF_FLOAT`, `GL_INT_2_10_10_10_REV`, or `GL_UNSIGNED_INT_2_10_10_10_REV`.

### Buffer Mapping Optimization
When modifying buffers frequently, desktop patterns like `glMapBuffer()` can cause severe driver stalls on mobile Tile-Based Deferred Rendering (TBDR) architectures.
*   **Preferred GLES 3.2 Method:** Use `glMapBufferRange()` combined with flags `GL_MAP_INVALIDATE_BUFFER_BIT` or `GL_MAP_UNSYNCHRONIZED_BIT`.
*   Alternatively, clear and reallocate using `glBufferData(..., NULL, GL_STREAM_DRAW)` before writing fresh data.

---

## 4. GLSL Shading Language (ESSL 3.20)

Desktop GLSL must be updated to **ESSL 3.20** for OpenGL ES 3.2 compliance.

### Shading Language Headers
Change your shader macro declarations at the absolute first line of your source files:
```glsl
// Desktop OpenGL
#version 450 core or #version 460 core

// OpenGL ES 3.2
#version 320 es
```

### Mandated Precision Qualifiers
In ESSL, fragment shaders **require** explicit floating-point precision declarations. If you omit this, your shader will fail to compile.

```glsl
#version 320 es
precision highp float;   // Mandated for fragment shaders requiring high accuracy
precision mediump int;   // Saves bandwidth/power where high precision isn't vital

in vec2 vTexCoord;
out vec4 fragColor;

void main() {
    fragColor = vec4(1.0);
}
```
*   `highp`: 32-bit float (Positions, complex math).
*   `mediump`: 16-bit float (Texture coordinates, normals). Saves power.
*   `lowp`: 10-bit fixed point (Simple vertex colors, ranges $[-2, 2]$). Highly optimized for performance.

### Inputs, Outputs, and Built-ins
Modern GLES 3.2 utilizes `in` and `out` keywords identically to modern desktop OpenGL. However, be aware of built-in name changes if porting from older code bases:

*   Texture samples use the unified `texture()` function (do not use legacy `texture2D()` or `textureCube()`).
*   Vertex shader outputs to fragment shaders do not support `gl_ClipDistance` unless the `GL_EXT_clip_cull_distance` extension is explicitly active and supported.

---

## 5. Textures, Framebuffers, & Formats

Mobile GPUs rely heavily on texture compression to save memory bandwidth (the primary performance bottleneck on mobile).

### ❌ Unsupported Texture Targets
*   `GL_TEXTURE_1D` and `GL_TEXTURE_1D_ARRAY`: Emulate with a `GL_TEXTURE_2D` where the height dimension is set to `1`.
*   `GL_TEXTURE_RECTANGLE`: Convert to standard `GL_TEXTURE_2D` utilizing normalized coordinates ($[0.0, 1.0]$).

### 📦 Mandatory Texture Compression Formats
While desktop systems widely use BC/DXTC (DirectX) formats, GLES 3.2 guarantees native hardware support for:
*   **ASTC (Adaptive Scalable Texture Compression):** Use `GL_COMPRESSED_RGBA_ASTC_*` formats. Offers the best quality-to-size ratio on modern mobile SOCs.
*   **ETC2 / EAC:** Standard fallback formats guaranteed by the GLES 3.0+ specification.

### 🖼️ Framebuffer Restrictions
*   Always match internal texture formats exactly. Mixed-format attachments (e.g., binding an `RGBA8` color buffer alongside a complex integer depth-stencil format) can fail validation on specific mobile drivers.
*   Query your maximum color attachments via `glGetIntegerv(GL_MAX_COLOR_ATTACHMENTS, &maxCmd)`. GLES 3.2 guarantees a minimum of **4** simultaneous attachments, whereas modern desktop platforms usually guarantee 8.

---

## 6. Mobile Performance Checklist (TBDR Architectures)

Most GLES 3.2 devices use **Tile-Based Deferred Renderers** (Apple, ARM Mali, Qualcomm Adreno) rather than desktop Immediate Mode Renderers (NVIDIA, AMD). Adjust your code logic using these rules:

1.  **Always `glClear()` Everything:** On a mobile device, calling `glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT)` signals the GPU driver to discard the previous tile contents entirely. Omitting a clear forces the device to waste massive bandwidth copying old frame content from system RAM into high-speed on-chip cache.
2.  **Invalidate Unneeded Buffers:** If you do not need the depth or stencil data after rendering a frame, call `glInvalidateFramebuffer()` before unbinding your FBO. This prevents the GPU from burning power writing depth values back out to the main system memory.
3.  **Minimize State Changes:** Group draw calls tightly by shader program, then by texture bindings, and finally by uniform configurations. State changes are significantly more taxing on mobile platforms than on desktop environments.
