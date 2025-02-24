# KatharaP4AAS

IGNITE 5.0 Project - Unibo 2024/2025.

Work in progress.

## To start up:
1. Goes to "App" folder and execute "gradle build". Verify that in folder "kathara/aas_project/" there must be a Jar called P4_AAS.jar.
2. If everything ok: Launch Kathara inside folder "kathara/": kathara lstart
3. All shells will deploy necessary software: NB. Switch1, Switch2, CNT1 and CNT2 Shells will execute in background necessary P4 software, so they won't display anything in each shell.
4. Into "aas_project" shell execute "java -jar P4_AAS.jar" to start up the Web UI.


COUNTERS: https://cornell-pl.github.io/cs6114/lecture07.html
Compilazione file p4: p4c --target bmv2 --p4runtime-files standard.p4info.txt standard.p4
Aggiunto counters solo con programma STANDARD
v1Model definitions: https://github.com/p4lang/p4c/blob/main/p4include/v1model.p4
