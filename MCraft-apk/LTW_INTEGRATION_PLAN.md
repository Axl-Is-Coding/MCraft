# 🔧 LTW Resurrection Plan

**Goal:** Replace missing OpenGL ES renderer with LTW translation layer.

## 🎯 What We're Doing

Minosoft (OpenGL 3.x calls) → LTW Library → OpenGL ES 3.0 → Android GPU

## 📦 What We Need

1. **LTW AAR** - Download from PojavLauncher's LTW repo or build from source
2. **Native bridge** - Kotlin code to initialize LTW and pass GL calls
3. **Minosoft modification** - Hook Minosoft's GL calls into LTW instead of desktop GL

## 🔧 Step-by-Step Integration

### Phase 1: Add LTW to project
```gradle
// MCraft-apk/build.gradle
dependencies {
    implementation files('libs/ltw-release.aar')
}