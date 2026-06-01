# Minosoft Pipeline Migration Guide: OpenGL to OpenGL ES 3.2

This cheat sheet targets the architectural patterns found in voxel clients like **Minosoft** (Kotlin/Java). It covers the specific translation steps for high-throughput block mesh updates, memory bandwidth reduction, and mobile GPU optimization.

---

## 1. Architectural Strategy for Voxel Pipelines

### 🧵 Chunk Data Streaming & Buffer Orphanage
Desktop pipelines often map buffers long-term or reallocate on the fly. On mobile Tile-Based Deferred Renderers (TBDR), rewriting an in-use vertex buffer stalls the entire graphics pipeline.
*   **The Mobile Fix:** Implement **Buffer Orphanage**. Before writing updated chunk geometry via `glBufferSubData`, pass `NULL` to `glBufferData`. This tells the GLES driver to abandon the old memory block (which the GPU can finish draining safely) and gives you a fresh, clean memory block immediately.

### 📦 Low-Bandwidth Vertex Packing
Desktop Minecraft variants push 32-bit floats for everything. Mobile hardware is heavily constrained by memory bandwidth. You must compress Minosoft’s vertex layouts to prevent performance drops.

*   **Voxel Positions:** A standard sub-chunk is $16 \times 16 \times 16$. Do not use 32-bit floats. Use 4-byte packed arrays (`GL_UNSIGNED_BYTE` or `GL_BYTE`) using a vertex shader scaling matrix.
*   **Texture Atlas Coordinates (U, V):** Pack block textures into 16-bit integers (`GL_UNSIGNED_SHORT`) instead of full floats.
*   **Lightmaps (Block & Sky light):** Pack both 0–15 light values into a single 8-bit or 16-bit variable.

---

## 2. Direct API Substitution Dictionary

Use this table to swap desktop OpenGL functions and arguments for safe, compliant OpenGL ES 3.2 configurations.


| Desktop OpenGL Call | OpenGL ES 3.2 Equivalent | Notes & Implementation for Minosoft |
| :--- | :--- | :--- |
| `glGenVertexArrays(...)` | `glGenVertexArrays(...)` | Direct map, but must verify binding context initialization. |
| `glBindVertexArray(...)` | `glBindVertexArray(...)` | Core feature in GLES 3.0+. Required for chunk mesh batching. |
| `glPolygonMode(..., GL_LINE)` | **None** | Completely removed. For structural chunk wireframes (debug), change primitive type to `GL_LINES`. |
| `glAlphaFunc(GL_GREATER, 0.1f)` | **None** | Removed. Handled directly inside the fragment shader via `discard;`. |
| `glEnable(GL_ALPHA_TEST)` | **None** | Removed. Perform threshold operations directly in the ESSL shader. |
| `glEnable(GL_MULTISAMPLE)` | **Implicit** | Controlled at the native window/surface creation level (EGL / Android Surface). |
| `glTexImage2D(..., GL_TEXTURE_RECTANGLE, ...)`| `glTexImage2D(..., GL_TEXTURE_2D, ...)` | GLES does not support texture rectangles. Use normalized $(0.0 - 1.0)$ coordinates. |
| `double` / `GL_DOUBLE` | `float` / `GL_FLOAT` | GLES hardware drops double precision. Convert all matrix and vertex data down to 32-bit float. |
| `glMapBuffer(...)` | `glMapBufferRange(...)` | Use `glMapBufferRange` with `GL_MAP_INVALIDATE_BUFFER_BIT` to eliminate mobile CPU-GPU stalls. |
| `glDrawArraysInstanced(...)` | `glDrawArraysInstanced(...)` | Fully supported. Excellent for rendering massive counts of identical entities or blocks. |

---

## 3. Shader Conversions (GLSL 4.x $\rightarrow$ ESSL 3.20)

Minosoft block and fluid shaders must be explicitly updated to ESSL syntax rules.

### Header Declaration
```glsl
// Remove: #version 450 core
#version 320 es
```

### Voxel Alpha-Cutout (Leaves, Glass, Tall Grass)
Desktop fixed-function alpha testing is dead. Use explicit conditional pixel discarding inside the fragment shader.

```glsl
#version 320 es
precision highp float; // Required flag for GLES fragment shaders

in vec2 v_TexCoord;
in vec4 v_BlockLight;
out vec4 fragColor;

uniform sampler2D u_BlockAtlas;

void main() {
    vec4 texColor = texture(u_BlockAtlas, v_TexCoord);
    
    // Explicit alpha test replacement
    if (texColor.a < 0.26) {
        discard; 
    }
    
    fragColor = texColor * v_BlockLight;
}
```

---

## 4. Minecraft Texture Atlas & Mipmap Adjustments

### Preventing Atlas Bleeding
Because GLES texture sampling engines are highly aggressive at tile boundaries, wrapping block textures tightly inside a shared atlas sheet results in black lines or bleeding seams on long-distance terrain.
*   **The Fix:** Inject a **4-pixel edge pad (gutter)** matching the edge color around every single icon during atlas generation.
*   **Sampling:** Force coordinate calculations inside the vertex/fragment shader to clamp strictly to the interior bounding-box of the target block sub-texture.

---

## 5. Mobile Framework Optimization Checklist (TBDR)

1.  **Enforce Frame Clearing:** Always clear your color and depth buffers concurrently at the start of Minosoft's frame tick. 
    ```kotlin
    GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
    ```
    *Why?* If omitted, mobile chips waste immense bandwidth reading the previous frame’s stale data out of memory.
2.  **Invalidate Unused FBO Attachments:** After processing shadow mapping passes or final UI compositions, invalidate buffers you don't intend to read next frame.
    ```kotlin
    val attachments = intArrayOf(GLES32.GL_DEPTH_ATTACHMENT)
    GLES32.glInvalidateFramebuffer(GLES32.GL_FRAMEBUFFER, 1, attachments, 0)
    ```
    This deletes tile depth cache instantly on-chip, skipping expensive RAM writes.

P.S: I ai generated this. Don't use. I will be reading and changing it later