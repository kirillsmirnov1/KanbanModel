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
    public static final StageType[] stagesReverse; // Стадии в обратном порядке

    static {
        StageType[] stagesReverse_dummy = values();
        ArrayUtils.reverse(stagesReverse_dummy);
        stagesReverse = stagesReverse_dummy;
    }

    StageType nextStage(){ //TODO использовать это в следующей стадии в таске
        if(this == DEPLOYMENT) return DEPLOYMENT;
        return values()[this.ordinal()+1];
    }

    static public String[] toSortedStringArray(HashMap<StageType, ? extends Object> stages){
        ArrayList<String> strings = new ArrayList<>();
        for(int i=0; i < values().length; ++i){
            if(stages.containsKey(StageType.values()[i]))
                strings.add(stages.get(StageType.values()[i]).toString());
        }

        return strings.toArray(new String[1]);
    }
}
