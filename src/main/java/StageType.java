import java.util.ArrayList;
import java.util.HashMap;

//Стадии работы
public enum StageType {
    BACKLOG,         // Поступившая задача
    ANALYSIS,        // Анализ
    DESIGN,          // Проектирование
    IMPLEMENTATION,  // Реализация
    INTEGRATION,     // Интеграция
    DOCUMENTATION,   // Документация
    TESTING,         // Испытания
    DEPLOYMENT;      // Поставка

    static public String[] toSortedStringArray(HashMap<StageType, ? extends Object> stages){
        ArrayList<String> strings = new ArrayList<>();
        for(int i=0; i < values().length; ++i){
            if(stages.containsKey(StageType.values()[i]))
                strings.add(stages.get(StageType.values()[i]).toString());
        }

        return strings.toArray(new String[1]);
    }

    public static final StageType[] workStages = {ANALYSIS, DESIGN, IMPLEMENTATION, INTEGRATION, DOCUMENTATION, TESTING};
}
