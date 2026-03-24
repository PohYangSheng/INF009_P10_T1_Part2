package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

/**
 * Stores the chosen player character for the session.
 * Passed by constructor injection – no singleton.
 */
public class CharacterSelectionManager {

    public enum CharacterOption {
        BOY_CHILD    ("Boy Child",     "entities_images/boychild.png"),
        GIRL_CHILD   ("Girl Child",    "entities_images/girlchild.png"),
        MALE_ADULT   ("Male Adult",    "entities_images/maleadult.png"),
        GIRL_ADULT   ("Girl Adult",    "entities_images/girladult.png"),
        ELDERLY_MAN  ("Elderly Man",   "entities_images/elderlyman.png"),
        ELDERLY_WOMAN("Elderly Woman", "entities_images/elderlywoman.png");

        private final String displayName;
        private final String texturePath;

        CharacterOption(String displayName, String texturePath) {
            this.displayName = displayName;
            this.texturePath = texturePath;
        }
        public String getDisplayName() { return displayName; }
        public String getTexturePath() { return texturePath; }
    }

    private CharacterOption selected = CharacterOption.BOY_CHILD;

    public CharacterOption[] getAvailableCharacters()                      { return CharacterOption.values(); }
    public CharacterOption   getSelectedCharacter()                        { return selected; }
    public void              setSelectedCharacter(CharacterOption option)  { if (option != null) selected = option; }
    public String            getSelectedTexturePath()                      { return selected.getTexturePath(); }
}
