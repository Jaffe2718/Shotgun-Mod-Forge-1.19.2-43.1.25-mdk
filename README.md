# 霰弹枪模组说明文档

## 使用环境规范

### MDK运行要求

| 项目                        | 参数                   |
|:------------------------- | -------------------- |
| Java Devalopment kit      | 17.x / 18.x          |
| Gradle                    | 由项目自动下载              |
| IDE（推荐JetBrains IntelliJ） | Community / Ultimate |

### 模组运行要求

| 项目                      | 参数              |
| ----------------------- | --------------- |
| Java Devalopment kit    | 17.x / 18.x     |
| Minecraft: Java Edition | 1.19.x          |
| Forge                   | 推荐最新版或者稳定版      |
| 启动器                     | 推荐BakaXL Public |

### 玩法说明

> 霰弹枪的合成需要铁粒、木材、绊线钩和模组特有的碳化合金，具体合成表如下：
> 
> <img src="make_shotgun.png" title="" alt="images" data-align="center">
> 
> 而碳化合金的合成需要铜锭、金锭、铁锭，还需要煤炭块，合成方式如下：
> 
> <img title="" src="make_carbide_alloy.png" alt="images" data-align="center">
> 
> 霰弹枪使用火药作为弹药，总耐久度100。

## 项目结构 

```
\---src
    +---generated
    |   \---resources
    +---main
    |   +---java                                  // 代码区
    |   |   \---mod
    |   |       \---jaffe2718
    |   |           \---shotgun                   
    |   |               |   MainClass.java        // 模组的主类
    |   |               |
    |   |               +---entity                // 实体类
    |   |               |       GrapeshotEntity.java
    |   |               |
    |   |               +---init                  // 注册类，注册游戏元素
    |   |               |   |   EntityInit.java
    |   |               |   |   ItemInit.java
    |   |               |   |   SoundInit.java
    |   |               |   |
    |   |               |   +---modelinit        // 加载模型类
    |   |               |   |       ShotgunModelOverrides.java // 动态模型
    |   |               |   |
    |   |               |   \---RendererInit     // 渲染类
    |   |               |           RendererEntityInit.java  //注册实体渲染
    |   |               |
    |   |               \---item                 // 物品类
    |   |                       CarbideAlloyItem.java
    |   |                       ShotgunItem.java
    |   |
    |   \---resources                            // 资源区
    |       |   pack.mcmeta                      // 配置文件、包的元数据
    |       |   shotgunicon.png                  // 模组的icon
    |       |
    |       +---assets
    |       |   \---shotgun
    |       |       |   sounds.json              // 用于声明音效文件
    |       |       |
    |       |       +---lang                     // 将模组的元素进行翻译
    |       |       |       en_us.json
    |       |       |       zh_cn.json
    |       |       |
    |       |       +---models
    |       |       |   +---custom               // 原始模型，用于Overrides
    |       |       |   |       shotgun_custom.json
    |       |       |   |       shotgun_loaded.json
    |       |       |   |       shotgun_loading_0.json
    |       |       |   |       shotgun_loading_1.json
    |       |       |   |       shotgun_loading_2.json
    |       |       |   |
    |       |       |   \---item                 // 物品模型
    |       |       |           carbide_alloy.json
    |       |       |           shotgun.json
    |       |       |
    |       |       +---sounds                   // 音效文件
    |       |       |       grapeshot_hit.ogg
    |       |       |       shotgun_can_load.ogg
    |       |       |       shotgun_loaded.ogg
    |       |       |       shotgun_loading.ogg
    |       |       |       shotgun_shot.ogg
    |       |       |
    |       |       \---textures                // 纹理
    |       |           \---item                // 物品纹理
    |       |                   carbide_alloy.png
    |       |                   shotgun.png
    |       |                   shotgun_loaded.png
    |       |
    |       +---data                            // 数据包
    |       |   \---shotgun
    |       |       \---recipes                 // 合成表
    |       |               alchemy.json
    |       |               shotgun_formula.json
    |       |
    |       \---META-INF
    |               mods.toml                   // 模组信息元数据
    |
    \---test
        +---java
        \---resources
```

### 项目类图展示

![images](uml.png)

# 下面引用自Forge MDK的官方使用说明

Source installation information for modders
-------------------------------------------

This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:

1. Run the following command: `gradlew genEclipseRuns` (`./gradlew genEclipseRuns` if you are on Mac/Linux)
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:

1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `gradlew genIntellijRuns` (`./gradlew genIntellijRuns` if you are on Mac/Linux)
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
=============================

By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources:
=========================

Community Documentation: https://mcforge.readthedocs.io/en/latest/gettingstarted/  
LexManos' Install Video: https://www.youtube.com/watch?v=8VEdtQLuLO0  
Forge Forum: https://forums.minecraftforge.net/  
Forge Discord: https://discord.gg/UvedJ9m  
