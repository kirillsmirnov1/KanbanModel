package trulden.com.vk.KanbanModel.model;

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

    public static final StageType[] workStages = {ANALYSIS, DESIGN, IMPLEMENTATION, INTEGRATION, DOCUMENTATION, TESTING};
    public static final StageType[] workStagesReverse; // Рабочие стадии в обратном порядке
    public static final StageType[] stagesReverse; // Стадии в обратном порядке

    static {
        StageType[] stagesReverse_dummy = values();
        ArrayUtils.reverse(stagesReverse_dummy);
        stagesReverse = stagesReverse_dummy.clone();
        stagesReverse_dummy = workStages.clone();
        ArrayUtils.reverse(stagesReverse_dummy);
        workStagesReverse = stagesReverse_dummy;
    }

    public StageType nextStage(){
        if(this == DEPLOYMENT) return DEPLOYMENT;
        return values()[this.ordinal()+1];
    }

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
