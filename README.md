The mod solves the problem of not being able to use item model definitions for the same item from multiple resource packs: It changes the method of loading models into the game's memory and how the model is selected for rendering.
<div align="center"><img src="https://cdn.modrinth.com/data/cached_images/d80d46a118dd42995f5608cbdae164da9f4f2ff3.png" alt="shceme"></div>

### How it works?
When the game needs to select a model for rendering, it goes through each resource pack in order and checks whether the model meets certain conditions. If it does not, the mod “delegates” this task to the next pack, and so on until it selects the vanilla pack.
<div align="center"><img src="https://i.postimg.cc/GmZsTRrg/Untitled-Diagram-Page-1-drawio.png" alt="shceme"></div>

## it BREAKS resource packs
Unfortunately, because some resource packs rely on the default behavior of fallbacks, some models may not display correctly (especially in the GUI). Therefore, creators will have to slightly adapt their packs to work with the mod. This process will be described in the Wiki (coming soon).

### Modpacks
The license allows you to freely use this mod in any public builds, but I would be very happy if you told as many people as possible about the mod, because it is very useful, not many people know about it, and I worked very hard on it.