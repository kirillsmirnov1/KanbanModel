Kanban Model
=======

Kanban Model показывает: 

1. Как карточки пролетают по доске, сотрудники устают, а CFD-диаграмма разрастается с каждый днём.
2. Как изменение WIP-лимитов влияет на количество выполненных задач и время выполнения этих задач.

### Запуск

* Собрать 
* Заполнить *init.json* и *scenarios.json* желаемыми параметрами
* Запустить и наслаждаться

### Инициализация

В *init.json* определяются параметры, которые задают поведение модели в целом. 

* *NUMBER_OF_DAYS* − Количество дней, которое будет работать каждый сценарий
* *NUMBER_OF_WORKERS* − Количество сотрудников
* *UI_REFRESH_DELAY* − Задержка отображения изменений на канбан-доске, мс. Не менее 5.
* *PRINTING_RESULTS_TO_CONSOLE* − Печать результатов работы модели в консоль. Выводит статус доски раз в день.
* *scenariosPath* − Путь к файлу сценариев
* showBoard − Отображение канбан-доски

### Сценарии

Сценарии работы модели описываются в *JSON*-файле. Массив элементов должен называться *scenarios* и содержать следующие элементы:

* *maxWorkerEnergy* − время работы сотрудника

* *taskChangePenalty* − штраф за смену задачи в диапазоне от 0.0 до 1.0. Часть *maxWorkerEnergy* , которую работнику нужно потратить на то чтобы разобраться в новой задаче.

* *WIPLimits* − массив в виде строки из 8 WIP-лимитов стадий.

  > "WIPLimits" : "[4, 4, 4, 4, 4, 4, 4, 1000000]",

* *deploymentFrequency* − частота деплоймента в днях.

Сценариев может быть сколько угодно.

