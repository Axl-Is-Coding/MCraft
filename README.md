# ⚠️ PROJECT STATUS: RESURRECTING / EXPERIMENTAL ⚠️

**This project is being slowly revived.**

The architecture, Minosoft modifications, and launcher backend are complete. What was missing is the OpenGL ES renderer implementation — but we're now exploring **LTW (Large Thin Wrapper)** as a translation layer instead of writing a renderer from scratch.

**Why it stalled before:**
- Required deeper OpenGL ES knowledge
- Kotlin expertise gap
- Limited PC access

**What's happening now:**
- Researching LTW (LGPL v3) to translate OpenGL 3.x → OpenGL ES 3.0
- MCraft will remain open source under MIT

**Current status:** Exploring solutions, experimenting, learning.


---

*Original vision: Native Android port of Minecraft Java Edition* 
---


# MCraft - Minecraft Java Edition for Android

> *"Java Edition in your pocket. Not Bedrock. Not a launcher. The real thing."*

## 🎯 What is MCraft?

MCraft is a **native Android port** of Minecraft Java Edition, built on top of [Minosoft](https://github.com/Bixilon/minosoft). This is not a launcher that runs official Minecraft JARs—this is a clean-room implementation that runs natively on your phone.

## ✨ Features (Planned)

- ✅ Native Android port (OpenGL ES, not emulation)
- ✅ Flat world singleplayer
- ✅ Touch controls (Bedrock-style)
- ✅ Open source + community-driven
- 🔮 Lua modding API
- 🔮 Multi-version support (1.7 - 1.20.4)
- 🔮 Full singleplayer world generation

## 🛠️ Current Status

**Research phase.** Investigating LTW as a renderer backend. No APK yet.

```

MCraft/
├── KnowledgeBase/     # Documentation + code comments
├── Original/          # Pure Minosoft 
├── Modification/      # Modified Minosoft
└── Reconstruction/    # Future standalone structure

```

## 📱 Target Device

**Huawei ART-L28** (2018 budget tablet)
- Kirin 710, 3-4GB RAM, Mali-G51 GPU
- If it runs on this, it runs on anything

## 🤝 Open Source & License

**MCraft is MIT licensed.**  
**LTW (renderer backend) is LGPL v3.**

This means:
- ✅ You can use MCraft code anywhere, even in closed-source projects
- ✅ LTW remains free and open — modifications to LTW must stay LGPL
- ✅ Your mods and addons can use any license
- ❌ You cannot remove LTW's LGPL notice or prevent relinking

**Why LGPL v3?** It lets us use LTW's powerful OpenGL translation without forcing MCraft to become GPL.

[Full LGPL v3 text](./COPYING.LESSER)

## 📜 Credits

- **Minosoft** - Moritz Zwerger (bixilon) - The foundation
- **LTW (Large Thin Wrapper)** - The renderer translation layer that made revival possible
- **MCraft** - WhoisAxl? (Axl-Is-Coding) - The Android port + resurrection effort

---

*"Rome wasn't built in a day, and neither is a Minecraft Java mobile port."*

*"But we found some bricks."*
