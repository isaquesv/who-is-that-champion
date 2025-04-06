package utils;

import java.util.Arrays;

/**
 * Classe utilitária responsável por realizar manipulações de texto e traduções
 * relacionadas aos dados dos campeões
 */
public class TextManipulation {

    /**
     * Coloca a primeira letra da string em maiúscula
     */
    public static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Traduz o gênero do campeão para o idioma especificado
     */
    public static String getTranslateGender(String gender, String language) {
        String translatedGender = gender;

        if (language.equals("pt_BR")) {
            if (gender.equals("Male")) {
                translatedGender = "Masculino";
            } else if (gender.equals("Female")) {
                translatedGender = "Feminino";
            } else if (gender.equals("Other")) {
                translatedGender = "Outro";
            }
        } else if (language.equals("es_ES")) {
            if (gender.equals("Male")) {
                translatedGender = "Masculino";
            } else if (gender.equals("Female")) {
                translatedGender = "Femenino";
            } else if (gender.equals("Other")) {
                translatedGender = "Otro";
            }
        }

        return translatedGender;
    }

    /**
     * Traduz a rota do campeão para o idioma especificado
     */
    public static String getTranslateLane(String lane, String language) {
        String translatedLane = lane;

        if (language.equals("pt_BR")) {
            if (lane.equals("Top")) {
                translatedLane = "Topo";
            } else if (lane.equals("Jungle")) {
                translatedLane = "Selva";
            } else if (lane.equals("Mid")) {
                translatedLane = "Meio";
            } else if (lane.equals("Bottom")) {
                translatedLane = "Atirador";
            } else if (lane.equals("Support")) {
                translatedLane = "Suporte";
            }
        } else if (language.equals("es_ES")) {
            if (lane.equals("Top")) {
                translatedLane = "Superior";
            } else if (lane.equals("Jungle")) {
                translatedLane = "Jungla";
            } else if (lane.equals("Mid")) {
                translatedLane = "Central";
            } else if (lane.equals("Bottom")) {
                translatedLane = "Inferior";
            } else if (lane.equals("Support")) {
                translatedLane = "Soporte";
            }
        }

        return translatedLane;
    }

    /**
     * Traduz a região de origem do campeão para o idioma especificado
     */
    public static String getTranslateRegion(String region, String language) {
        String translatedRegion = region;
        String[] translatableRegions = {"Mount-targon", "Void", "Bandle-city", "Shadow-isles", "Bilgewater"};

        if (Arrays.asList(translatableRegions).contains(region)) {
            if (language.equals("pt_BR")) {
                if (region.equals("Mount-targon")) {
                    translatedRegion = "Monte Targon";
                } else if (region.equals("Void")) {
                    translatedRegion = "Vazio";
                } else if (region.equals("Bandle-city")) {
                    translatedRegion = "Bandópolis";
                } else if (region.equals("Shadow-isles")) {
                    translatedRegion = "Ilhas das Sombras";
                } else if (region.equals("Bilgewater")) {
                    translatedRegion = "Águas de Sentina";
                }
            } else if (language.equals("es_ES")) {
                if (region.equals("Mount-targon")) {
                    translatedRegion = "Monte Targon";
                } else if (region.equals("Void")) {
                    translatedRegion = "El Vacío";
                } else if (region.equals("Bandle-city")) {
                    translatedRegion = "Ciudad de Bandle";
                } else if (region.equals("Shadow-isles")) {
                    translatedRegion = "Islas de la Sombra";
                } else if (region.equals("Bilgewater")) {
                    translatedRegion = "Aguas Estancadas";
                }
            }
        }

        return translatedRegion;
    }

    /**
     * Traduz a função do campeão para o idioma especificado
     */
    public static String getTranslateFunction(String function, String language) {
        String translatedFunction = function;

        if (language.equals("pt_BR")) {
            if (function.equals("Fighter")) {
                translatedFunction = "Lutador";
            } else if (function.equals("Assassin")) {
                translatedFunction = "Assassino";
            } else if (function.equals("Mage")) {
                translatedFunction = "Mago";
            } else if (function.equals("Marksman")) {
                translatedFunction = "Atirador";
            } else if (function.equals("Tank")) {
                translatedFunction = "Tanque";
            } else if (function.equals("Support")) {
                translatedFunction = "Suporte";
            }
        } else if (language.equals("es_ES")) {
            if (function.equals("Fighter")) {
                translatedFunction = "Luchador";
            } else if (function.equals("Assassin")) {
                translatedFunction = "Asesino";
            } else if (function.equals("Mage")) {
                translatedFunction = "Mago";
            } else if (function.equals("Marksman")) {
                translatedFunction = "Tirador";
            } else if (function.equals("Tank")) {
                translatedFunction = "Tanque";
            } else if (function.equals("Support")) {
                translatedFunction = "Soporte";
            }
        }

        return translatedFunction;
    }

    /**
     * Traduz o tipo de alcance do campeão para o idioma especificado
     */
    public static String getTranslateRangeType(String rangeType, String language) {
        String translatedRangeType = rangeType;

        if (language.equals("pt_BR")) {
            if (rangeType.equals("Close")) {
                translatedRangeType = "Corpo-a-Corpo";
            } else if (rangeType.equals("Range")) {
                translatedRangeType = "Longo Alcance";
            }
        } else if (language.equals("es_ES")) {
            if (rangeType.equals("Close")) {
                translatedRangeType = "Cuerpo a Cuerpo";
            } else if (rangeType.equals("Range")) {
                translatedRangeType = "Alcance Largo";
            }
        } else {
            if (rangeType.equals("Close")) {
                translatedRangeType = "Melee";
            } else if (rangeType.equals("Range")) {
                translatedRangeType = "Ranged";
            }
        }

        return translatedRangeType;
    }
}
