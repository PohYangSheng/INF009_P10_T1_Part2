package io.github.some_example_name.lwjgl3.application_classes.utilities;

/**
 * Stores the currently selected player character across scenes.
 */
public class CharacterSelectionManager {
    public enum CharacterOption {
        BOY_CHILD("Boy Child", "entities_images/boychild.png"),
        GIRL_CHILD("Girl Child", "entities_images/girlchild.png"),
        MALE_ADULT("Male Adult", "entities_images/maleadult.png"),
        GIRL_ADULT("Girl Adult", "entities_images/girladult.png"),
        ELDERLY_MAN("Elderly Man", "entities_images/elderlyman.png"),
        ELDERLY_WOMAN("Elderly Woman", "entities_images/elderlywoman.png");

        private final String displayName;
        private final String texturePath;

        CharacterOption(String displayName, String texturePath) {
            this.displayName = displayName;
            this.texturePath = texturePath;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getTexturePath() {
            return texturePath;
        }
    }

    private static CharacterSelectionManager instance;

    private CharacterOption selectedCharacter = CharacterOption.BOY_CHILD;

    private CharacterSelectionManager() {
    }

    public static CharacterSelectionManager getInstance() {
        if (instance == null) {
            instance = new CharacterSelectionManager();
        }
        return instance;
    }

    public CharacterOption[] getAvailableCharacters() {
        return CharacterOption.values();
    }

    public CharacterOption getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(CharacterOption selectedCharacter) {
        if (selectedCharacter != null) {
            this.selectedCharacter = selectedCharacter;
        }
    }

    public String getSelectedTexturePath() {
        return selectedCharacter.getTexturePath();
    }
}