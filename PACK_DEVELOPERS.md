# How it works?
When the game needs to select a model for rendering, it goes through each resource pack in order and checks whether the model meets certain conditions. If it does not, the mod “delegates” this task to the next pack, and so on until it selects the vanilla pack.
<div align="center"><img src="https://i.postimg.cc/GmZsTRrg/Untitled-Diagram-Page-1-drawio.png" alt="shceme"></div>

# Maniupulating delegation
The mod provides a way to manipulate delegation for models, which allows you to make some models delegate:

## `select` and `range_select` models
both models have new parameter `delegate`, by default it is `true`. if `false`, it breaks the delegation tests form current model, and this model will render

## `composite` model (1.2.0+)
has new parameter `delegate_strategy`:
1. `one_do_delegate` (default) - if one of the children models is `delegate` when entire composite model will delegate
2. `one_cancel_delegate` - if one of the children models is `delegate` when entire composite model will not delegate
3. `not_delegate` - composite model will not delegate, even if some of the children models are delegate

## About some non-obvious behaviors
 - If the `conditional` model is root, then its on_false model will be considered as a "fallback"
 - `empty` model is never delegate
 - `model` item model will delegate if:
    1. model is "fallback"
    2. model link namespace and item model id namespace from `item_model` component of item is equal
    3. model link path contains item model id path from `item_model` component of item

## About logging and debug
The mod has a logging system that allows you to see how the model selection process works. You can enable it by typing `/rpf_toggle_debug` in the chat. There are several
 types of logs:
- `ALLOW_UPDATE`: model passed test and will be rendered
- `DELEGATE`: model did not pass test and will delegate to the next pack
- `NEXT_TEST`: model delegates testing to children model
- `NEXT_TEST_FALLBACK`: model delegates testing to fallback model
- `INFO`: some useful information about model loading and delegation
- `ERROR`: some errors if model not found or some parameters are wrong.

this info (except errors) will be printed only once per model (form `item_model` component). to show it again, you need to press F3 + T to reload resource packs.
