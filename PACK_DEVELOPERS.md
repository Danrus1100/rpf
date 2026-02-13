# How it works?
When the game needs to select a model for rendering, it goes through each resource pack in order and checks whether the model meets certain conditions. If it does not, the mod “delegates” this task to the next pack, and so on until it selects the vanilla pack.
<div align="center"><img src="https://i.postimg.cc/GmZsTRrg/Untitled-Diagram-Page-1-drawio.png" alt="shceme"></div>

# Maniupulating delegation
The mod provides a way to manipulate delegation for models, which allows you to make some models delegate:

## `select` and `range_select` models
both models have new parameter `delegate`, by default it is `true`. if `false`, it breaks the delegation tests form current model, and this model will render

## `composite` model
has new parameter `delegate_strategy`:
1. `one_do_delegate` (default) - if one of the children models is `delegate` when entire composite model will delegate
2. `one_cancel_delegate` - if one of the children models is `delegate` when entire composite model will not delegate
3. `not_delegate` - composite model will not delegate, even if some of the children models are delegate