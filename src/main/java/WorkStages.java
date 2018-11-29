import java.util.HashMap;

//Стадии работы
public enum  WorkStages {
    BACKLOG,         // Поступившая задача
    ANALYSIS,        // Анализ
    DESIGN,          // Проектирование
    IMPLEMENTATION,  // Реализация
    INTEGRATION,     // Интеграция
    DOCUMENTATION,   // Документация
    TESTING,         // Испытания
    DEPLOYMENT;      // Поставка

    static public String[] toSortedStringArray(HashMap<WorkStages, Stage> stages){
        String[] strings = new String[values().length];
        for(int i=0; i < values().length; ++i){
            strings[i] = stages.get(WorkStages.values()[i]).toString();
        }

        return strings;
    }
}
