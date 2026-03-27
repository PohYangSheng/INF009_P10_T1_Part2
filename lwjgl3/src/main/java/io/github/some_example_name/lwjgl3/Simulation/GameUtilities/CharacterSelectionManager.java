package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

// stores which character the player picked
public class CharacterSelectionManager {

    public enum CharacterOption {
        BOY_CHILD    ("Boy Child",     "sprite_assets/hero_boy.png"),
        GIRL_CHILD   ("Girl Child",    "sprite_assets/hero_girl.png"),
        MALE_ADULT   ("Male Adult",    "sprite_assets/adult_man.png"),
        GIRL_ADULT   ("Girl Adult",    "sprite_assets/adult_woman.png"),
        ELDERLY_MAN  ("Elderly Man",   "sprite_assets/senior_man.png"),
        ELDERLY_WOMAN("Elderly Woman", "sprite_assets/senior_woman.png");

        private final String displayName;
        private final String texturePath;

        CharacterOption(String displayName, String texturePath) {
            this.displayName = displayName;
            this.texturePath = texturePath;
        }
        // getter for display name
        public String getDisplayName() {
            return displayName;
        }
        // getter for texture path
        public String getTexturePath() {
            return texturePath;
        }
    }

    private CharacterOption selected = CharacterOption.BOY_CHILD;

    // getter for list of characters that can be chosen in the avatar selection scene
    public CharacterOption[] getAvailableCharacters() {
        return CharacterOption.values();
    }
    // getter for selected character
    public CharacterOption   getSelectedCharacter() {
        return selected;
    }
    // set selected character
    public void              setSelectedCharacter(CharacterOption option) {
        if (option != null) selected = option;
    }
    // getter for selected texture path
    public String            getSelectedTexturePath() {
        return selected.getTexturePath();
    }
}
