package com.randomidentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class NameGenerator {

    private final RandomIdentityPlugin plugin;
    private final Random random = new Random();

    private final List<String> adjectives = new ArrayList<>();
    private final List<String> nouns = new ArrayList<>();

    public NameGenerator(RandomIdentityPlugin plugin) {
        this.plugin = plugin;

        loadAdjectives();
        loadNouns();
    }

    public String generate(Set<String> usedNames) {

        int maxAttempts = plugin.getConfig().getInt(
                "name.max-generation-attempts",
                100
        );

        double adjectiveChance = plugin.getConfig().getDouble(
                "name.adjective-chance",
                0.70
        );

        double numberChance = plugin.getConfig().getDouble(
                "name.number-chance",
                0.55
        );

        for (int attempt = 0; attempt < maxAttempts; attempt++) {

            String adjective =
                    adjectives.get(random.nextInt(adjectives.size()));

            String noun =
                    nouns.get(random.nextInt(nouns.size()));

            boolean useAdjective =
                    random.nextDouble() < adjectiveChance;

            boolean useNumber =
                    random.nextDouble() < numberChance;

            StringBuilder name = new StringBuilder();

            if (useAdjective) {
                name.append(adjective);
            }

            name.append(noun);

            if (useNumber) {

                int min = plugin.getConfig().getInt(
                        "name.number.min",
                        1
                );

                int max = plugin.getConfig().getInt(
                        "name.number.max",
                        999
                );

                if (max < min) {
                    max = min;
                }

                int number = min + random.nextInt(
                        max - min + 1
                );

                name.append(number);
            }

            String result = name.toString();

            /*
             * Minecraft Java usernames can be at most
             * 16 characters long.
             */
            if (result.length() > 16) {
                continue;
            }

            String lower = result.toLowerCase();

            if (!usedNames.contains(lower)) {
                return result;
            }
        }

        /*
         * Extremely unlikely fallback if all generated
         * names happened to be taken.
         */
        for (int i = 0; i < 10000; i++) {

            String fallback = "Player" + random.nextInt(100000);

            if (fallback.length() <= 16 &&
                    !usedNames.contains(fallback.toLowerCase())) {

                return fallback;
            }
        }

        return "Player" + System.currentTimeMillis() % 10000;
    }

    private void loadAdjectives() {

        String[] words = {

                "Cool",
                "Swift",
                "Tiny",
                "Happy",
                "Sleepy",
                "Shadow",
                "Golden",
                "Blue",
                "Red",
                "Green",
                "Dark",
                "Bright",
                "Silent",
                "Wild",
                "Brave",
                "Lucky",
                "Mighty",
                "Frosty",
                "Fiery",
                "Stormy",
                "Sneaky",
                "Rapid",
                "Epic",
                "Mega",
                "Ultra",
                "Cosmic",
                "Electric",
                "Mystic",
                "Ancient",
                "Royal",
                "Friendly",
                "Grumpy",
                "Fluffy",
                "Fuzzy",
                "Crazy",
                "Clever",
                "Witty",
                "Jolly",
                "Chill",
                "Frozen",
                "Burning",
                "Thunder",
                "Cloudy",
                "Dusty",
                "Rocky",
                "Fearless",
                "Lonely",
                "Brilliant",
                "Shiny",
                "Magic",
                "Gentle",
                "Angry",
                "Calm",
                "Hidden",
                "Unknown",
                "Mysterious",
                "Wandering",
                "Lost",
                "Hungry",
                "Bouncy",
                "Noisy",
                "Quiet",
                "Soft",
                "Hard",
                "Fast",
                "Slow",
                "Huge",
                "Giant",
                "Mini",
                "Silly",
                "Serious",
                "Dizzy",
                "Fancy",
                "Simple",
                "Weird",
                "Strange",
                "Normal",
                "Noble",
                "Fierce",
                "Savage",
                "Peaceful",
                "Icy",
                "Magma",
                "Volcanic",
                "Solar",
                "Lunar",
                "Stellar",
                "Galactic",
                "Quantum",
                "Digital",
                "Pixel",
                "Blocky",
                "Cubic",
                "Crafty",
                "Mining",
                "Nether",
                "Ender",
                "Overworld",
                "Village",
                "Creepy",
                "Spooky",
                "Haunted",
                "Phantom",
                "Ghostly",
                "Zombie",
                "Emerald",
                "Diamond",
                "Iron",
                "Copper",
                "Ruby",
                "Crystal",
                "Obsidian",
                "Quartz",
                "Amethyst",
                "Storm",
                "Thunderous",
                "Mighty",
                "Rapid",
                "Turbo",
                "Super",
                "Hyper",
                "Infinite",
                "Immortal",
                "Legendary",
                "Mythic",
                "Ultimate",
                "Secret",
                "Mystic",
                "Frozen",
                "Blazing",
                "Toxic",
                "Lucky",
                "Unlucky",
                "Chaotic",
                "Peaceful",
                "Cheerful",
                "Grumpy",
                "Sleepy",
                "Hungry",
                "Thirsty",
                "Sneaky",
                "Nimble",
                "Clever",
                "Curious",
                "Fearless",
                "Reckless",
                "Fearful",
                "Brave",
                "Bold",
                "Shy",
                "Wild",
                "Tamed",
                "Friendly",
                "Hostile"
        };

        for (String word : words) {
            adjectives.add(word);
        }
    }

    private void loadNouns() {

        String[] words = {

                "Penguin",
                "Dragon",
                "Fox",
                "Wolf",
                "Bear",
                "Tiger",
                "Lion",
                "Koala",
                "Panda",
                "Otter",
                "Badger",
                "Rabbit",
                "Bunny",
                "Cat",
                "Dog",
                "Horse",
                "Cow",
                "Sheep",
                "Goat",
                "Chicken",
                "Duck",
                "Frog",
                "Turtle",
                "Bee",
                "Spider",
                "Creeper",
                "Zombie",
                "Skeleton",
                "Enderman",
                "Witch",
                "Wizard",
                "Knight",
                "Ninja",
                "Pirate",
                "Robot",
                "Alien",
                "Astronaut",
                "Explorer",
                "Miner",
                "Builder",
                "Farmer",
                "Fisher",
                "Hunter",
                "Warrior",
                "Mage",
                "Archer",
                "Ranger",
                "King",
                "Queen",
                "Prince",
                "Princess",
                "Hero",
                "Villain",
                "Ghost",
                "Phantom",
                "Spirit",
                "Demon",
                "Angel",
                "Golem",
                "Slime",
                "Ghast",
                "Blaze",
                "Piglin",
                "Villager",
                "Pickaxe",
                "Sword",
                "Axe",
                "Bow",
                "Shield",
                "Hammer",
                "Spear",
                "Crown",
                "Helmet",
                "Boot",
                "Block",
                "Cube",
                "Pixel",
                "Diamond",
                "Emerald",
                "Ruby",
                "Crystal",
                "Amethyst",
                "Quartz",
                "Obsidian",
                "Torch",
                "Lantern",
                "Bucket",
                "Compass",
                "Map",
                "Book",
                "Scroll",
                "Chest",
                "Barrel",
                "Anvil",
                "Beacon",
                "Portal",
                "Castle",
                "Tower",
                "Village",
                "Island",
                "Mountain",
                "Forest",
                "Cave",
                "River",
                "Ocean",
                "Lake",
                "Cloud",
                "Star",
                "Moon",
                "Sun",
                "Comet",
                "Planet",
                "Galaxy",
                "Meteor",
                "Storm",
                "Thunder",
                "Lightning",
                "Fire",
                "Ice",
                "Snow",
                "Sand",
                "Stone",
                "Dirt",
                "Grass",
                "Tree",
                "Flower",
                "Mushroom",
                "Apple",
                "Cookie",
                "Cake",
                "Potato",
                "Carrot",
                "Melon",
                "Pumpkin",
                "Nugget",
                "Bacon",
                "Toast",
                "Pizza",
                "Taco",
                "Burger",
                "Pancake",
                "Noodle",
                "Goose",
                "Moose",
                "Squirrel",
                "Raccoon",
                "Hedgehog",
                "Hamster",
                "Monkey",
                "Gorilla",
                "Llama",
                "Camel",
                "Donkey",
                "Mule",
                "Parrot",
                "Butterfly",
                "Snail",
                "Crab",
                "Shark",
                "Whale",
                "Dolphin",
                "Squid",
                "Octopus",
                "Jellyfish",
                "Wyvern",
                "Griffin",
                "Phoenix",
                "Hydra",
                "Titan",
                "Giant",
                "Goblin",
                "Orc",
                "Elf",
                "Dwarf",
                "Viking",
                "Samurai",
                "Cowboy",
                "Detective",
                "Doctor",
                "Captain",
                "Pilot",
                "Traveler",
                "Adventurer",
                "Champion",
                "Legend",
                "Guardian",
                "Master",
                "Boss",
                "King",
                "Queen"
        };

        for (String word : words) {
            nouns.add(word);
        }
    }
}