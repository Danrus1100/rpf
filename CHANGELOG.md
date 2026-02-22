- Added `/rpf resolver`:
- This command shows the currently selected resolver. As of this release, there are 3 available:
    1. `rpf:v1` - Standard
    2. `minecraft:vanilla` - Vanilla game behavior
    3. `rpf:experimental` - Experimental resolver:
        - Unlike the standard version, the experimental one tests all models first. It then selects the one with the highest score (points are added or subtracted based on model nesting and whether it is a fallback). This strategy may perform better or worse depending on the number and complexity of resource packs.
- Improved stability and compatibility.