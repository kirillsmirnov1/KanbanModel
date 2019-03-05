package trulden.com.vk.KanbanModel.model.stage;

import org.apache.commons.lang3.ArrayUtils;

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

    // Стадии, на которых выполняется работа
    public static final StageType[] workStages = {ANALYSIS, DESIGN, IMPLEMENTATION, INTEGRATION, DOCUMENTATION, TESTING};
    // Рабочие стадии в обратном порядке
    public static final StageType[] workStagesReverse;
    // Стадии в обратном порядке
    public static final StageType[] stagesReverse;

    static {
        StageType[] stagesReverse_dummy = values();
        // Заполняю stagesReverse
        ArrayUtils.reverse(stagesReverse_dummy);
        stagesReverse = stagesReverse_dummy.clone();
        // Заполняю workStagesReverse
        stagesReverse_dummy = workStages.clone();
        ArrayUtils.reverse(stagesReverse_dummy);
        workStagesReverse = stagesReverse_dummy;
    }

    // Возвращает следующую по порядку стадию
    public StageType nextStage(){
        if(this == DEPLOYMENT) { // У деплоймента нет следующей стадии :<
            return DEPLOYMENT;
        }
        return values()[this.ordinal()+1];
    }

    // Принимает хэшмапы в которых ключ − этот класс, сортирует по порядку и возвращает массив строк значений
    static public String[] toSortedStringArray(HashMap<StageType, ?> stages){
        ArrayList<String> strings = new ArrayList<>();
        for(int i=0; i < values().length; ++i){
            if(stages.containsKey(StageType.values()[i]))
                strings.add(stages.get(StageType.values()[i]).toString());
        }
        return strings.toArray(new String[1]);
    }

    @Override
    public String toString(){
        switch (this){
            case BACKLOG:           return "Очередь";
            case ANALYSIS:          return "Анализ";
            case DESIGN:            return "Проектирование";
            case IMPLEMENTATION:    return "Реализация";
            case INTEGRATION:       return "Интеграция";
            case DOCUMENTATION:     return "Документация";
            case TESTING:           return "Тестирование";
            case DEPLOYMENT:        return "Поставка";
            default:                return super.toString();
        }
    }
}
