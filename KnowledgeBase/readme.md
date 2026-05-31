 MCraft Knowledge Base

> *"Understanding Minosoft, one comment at a time."*

## 📚 What is this?

This Knowledge Base is my personal documentation while reverse-engineering and porting **Minosoft** (a from-scratch Kotlin reimplementation of Minecraft Java Edition) to Android.

It contains:
- Code comments and explanations
- Architecture analysis
- Porting notes and challenges
- Insights that even the original author might have missed

## 🗂️ Structure

```

KnowledgeBase/
├── README.md (this file)
├── PlaySession-notes.md
├── Rendering-notes.md
├── Window-interface.md
├── OpenGlRenderSystem-notes.md
├── OS-Design-Differences/
│   ├── windows-vs-android.md
│   └── ...
└── ...

```

## 🎯 Goals

| Goal | Status |
|------|--------|
| Document core Minosoft files | 🔄 In progress |
| Understand the render pipeline | 🔄 In progress |
| Map OpenGL → OpenGL ES differences | 🔄 In progress |
| Create porting guide for future developers | 📅 Planned |
| Publish as reference for the community | 📅 Planned |

## 🔑 Key Files Documented

| File | What It Does | Documentation |
|------|--------------|---------------|
| PlaySession.kt | Master controller for game session | [Notes](./PlaySession-notes.md) |
| Rendering.kt | Main renderer entry point | [Notes](./Rendering-notes.md) |
| Window.kt | Platform window abstraction | [Notes](./Window-interface.md) |
| OpenGlRenderSystem.kt | Desktop OpenGL implementation | [Notes](./OpenGlRenderSystem-notes.md) |

## 📖 How to Use This

1. **For porters**: Use these notes to understand Minosoft before porting
2. **For modders**: Understand the architecture for Lua API development
3. **For me (future)**: Remember why I made certain decisions
4. **For the community**: Learn from my journey

## 📝 Notes Format

Each file follows this structure:

```markdown
# [FileName.kt]

## Overview
What does this file do?

## Key Functions
- `functionName()`: What it does
- `functionName()`: What it does

## Porting Notes
- What needs to change for Android
- What can stay the same

## Questions (to answer later)
- Why does this work this way?
```

🙏 Credits

· Minosoft: Moritz Zwerger (bixilon) - The foundation
· MCraft: axlittleYT - Documentation and Android port

---

"Documentation is not boring. It's a conversation with future you."