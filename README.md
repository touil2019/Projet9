Projet P9

TAUX DE COVERAGE du projet : 88%

Répertoire - doc : Diagramme de classes, configuration jenkins, taux de coverage
Répertoire - docker : conteneur docker de la base de données du projet

Requêtes SQL :

Création de la base de données et du jeux de données : 
    - /docker/dev/init/dev/docker-entrypoint-initdb.b/*
Les requêtes du projet :
    - /myerp-consumer/src/main/resources/com/dummy/myerp/consumer/sqlContext.xml
	
Initialisation du projet

1 - Docker :

Dans le fichier /docker/dev/docker-compose.yml modifier l'adresse IP en fonction de votre propre environnement

ports:
    - "[votre adresse ip]:9032:5432"
Lancer docker-compose (préalablement installé) puis lancer en commande gitBASH dans le repertoire du projet:

### Lancement

    cd docker/dev
    docker-compose up


### Arrêt

    cd docker/dev
    docker-compose stop


### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up
	
2 - Properties Database
Modifier l'adresse ip que vous avez paramétré dans docker

- myerp-consumer\src\main\resources\com\dummy\myerp\consumer\applicationContext.xml :
    myerp.datasource.driver=org.postgresql.Driver
    myerp.datasource.url=jdbc:postgresql://[votre adresse ip]:9032/db_myerp
    myerp.datasource.username=usr_myerp
    myerp.datasource.password=myerp
	
3 - Tests unitaires
Afin de vérifier le coverage on s'appuie sur jacoco les profiles à activer sont :

- test-business et test-consumer
Lancement commande maven :

- mvn clean verify
Lancement avec jenkins (voir paramétrage dans /doc/jenkinsConfig)

Pour le taux de coverage :

- soit par votre navigateur en ouvrant les fichiers :
    - myerp-business/target/site/jacoco/index.html
    - myerp-consumer/target/site/jacoco/index.html
    - myerp-model/target/site/jacoco/index.html
	
- soit avec jenkins
Configuration de Jenkins
Une fois Jenkins installé ( https://www.jenkins.io/doc/book/installing/), il est conseillé de
sauvegarder entre chaque étape.

1) Créer un nouveau projet freestyle
2) Dans l'onglet Gestion de code source: cocher Git, et entrer l'URL du repository git
3) Dans l'onglet Ce qui déclenche le build, cocher Scrutation de l'outil de version  
    et entrer * * * *  
4) Dans l'onglet Build: Ajouter une étape au build: Invoquer une cible de haut niveau  
    -choisir une version de maven (le plugin maven doit être installé)  
    -enter dans Cibles Maven: clean install -Ptest-business,test-consumer  
 La commande ci-dessus lance un build de maven avec le clean install, -P sert à  
 sélectionner les profils permettant de lancer les tests        
5) Dans l'onglet Actions à la suite du build: 
    - Ajouter une action après le buid: Publier le rapport des résultats des test JUnit 
    [vérifier que XML des rapports de test contient:**/target/surefire-reports/*.xml  
    et que le Health report amplification factor est à 1.0]  
    - De nouveau ajouter une action après le build: Record Jacoco coverage report  
    [vérifier que Path to exec files (e.g.: **/target/**.exec, **/jacoco.exec) soit à:  
    **/**.exec , que Path to class directories (e.g.: **/target/classDir, **/classes) soit à  
    **/classes , que Path to source directories (e.g.: **/mySourceFiles) soit à: **/src/main/java  ,
    que Inclusions (e.g.: **/*.java,**/*.groovy,**/*.gs) soit à: **/*.java,**/*.groovy,**/*.kt,**/*.kts]
	
Correctifs principaux

Absence de la configuration de la dataSource

Dans l'entité EcritureComptable, correction des méthodes getTotalCredit() && getTotalDebit() sur le format de retour 2 chiffres après la virgule
Dans l'entité EcritureComptable, correction de la méthode getTotalCredit() qui accédait à la méthode getDebit() au lieu de getCredit()
Dans l'entité EcritureComptable, correction de l'expression régulière qui était erronée
Dans le fichier sqlContext.xml, corriger la propriété SQLinsertListLigneEcritureComptable. Il manquait une virgule dans le INSERT entre les colonnes debit et credit
Dans la classe ComptabiliteManagerImpl, correction de la méthode updateEcritureComptable(). Ajouter la ligne this.checkEcritureComptable(pEcritureComptable); en haut afin de vérifier que la référence de l'écriture comptable respecte les règles de comptabilité 5 et 6
Dans la classe SpringRegistry de la couche business, modification de la variable CONTEXT_APPLI_LOCATION afin d'adapter le chemin d'accès au fichier bootstrapContext.xml qui est un conteneur Spring IoC, dans lequel on importe le businessContext.xml, consumerContext.xml et le datasourceContext.xml qui va redéfinir le bean dataSourceMYERP pour les tests
