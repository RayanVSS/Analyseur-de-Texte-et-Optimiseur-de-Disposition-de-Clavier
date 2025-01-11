# Analyseur de Texte et Optimiseur de Disposition de Clavier

# belhassen-bencheikh-projet-cpoo5-24-25
##  Sommaire

Fonctionnalités
Installation
Configuration des Dispositions de Clavier
Utilisation
Menu Principal
Options du Menu
Structure du Projet
Exemples
Dépannage
Contribuer
Licence


## Notre Projet
Notre projet est concue est conçue pour :

1. Analyser la fréquence des suites de caractères (monogrammes, bigrammes, trigrammes) dans des fichiers texte.

2. Évaluer l'efficacité des dispositions de clavier en fonction des analyses de fréquence.

3. Optimiser les dispositions de clavier pour améliorer la productivité et réduire la fatigue de frappe. 

Le projet utilise plusieurs patrons de conception pour assurer une architecture adapter et flexible pour  facilitant la maintenance 

## Fonctionnalités
- Analyseur de Fréquence de Suites de Caractères :
  - Analyse des monogrammes, bigrammes, trigrammes ou une combinaison des trois.
  - Prise en charge de multiples fichiers texte.
  - Génération de rapports en format JSON.

- Évaluateur de Disposition de Clavier :
  - Évaluation des dispositions de clavier existantes en fonction des statistiques de fréquence.
  - Affichage des scores d'efficacité pour chaque disposition évaluée.

- Optimiseur de Disposition de Clavier :
  - ILIASSSSS

- Affichage des Dispositions de Clavier Disponibles :
  - Liste des dispositions de clavier disponibles dans le répertoire dédié.

- Gestion des Résultats d'Analyse :
  - Vider les résultats précédents de l'analyseur.

- Gestion de l'Interface Utilisateur :
  - Interface en ligne de commande intuitive avec un menu principal pour naviguer entre les fonctionnalités.

## Installation

#### Prérequis
Java Development Kit (JDK) 21 ou supérieur
Gradle pour la gestion des dépendances et la compilation

#### Étapes d'Installation
Cloner le Répertoire du Projet :

```bash
git https://moule.informatique.univ-paris-diderot.fr/belhasse/belhassen-bencheikh-projet-cpoo5-24-25.git
```

Naviguer dans le Répertoire du Projet :
```bash
cd belhassen-bencheikh-projet-cpoo5-24-25
```

Compiler le Projet :

```bash
./gradlew build
```

Exécuter l'Application :
```bash
./gradlew run
```


## Intégration des Patron de conceptions

##### Rappel

- Décorateur : Ajoute dynamiquement des fonctionnalités sans modifier les classes existantes.

- Délégation : Sépare les responsabilités, facilitant ainsi la maintenance et l'extensibilité.

- Observateur/Observable : Permet une communication flexible entre les composants sans couplage étroit.

- Monteur (Builder) : Simplifie la création d'objets complexes, rendant le code client plus propre.

##### Patron de conceptions

Avec les implémenté des `patrons de conception`, notre projet gagne en robustesse et en flexibilité. 
Voici comment les différents patrons `s'intègrent` dans l'ensemble du projet :

####  Décorateur (LoggingFrequencyAnalyzerDecorator) :

Ajoute des fonctionnalités de journalisation ( ou Logging, enregistrer des informations sur l'exécution ) à l'analyseur de fréquence sans modifier sa structure.
Permet d'ajouter d'autres fonctionnalités similaires de manière modulaire.

####  Délégation (DataSource et TextFileDataSource) :

Sépare la logique de sélection des fichiers de l'analyseur de fréquence.
Facilite l'ajout de nouvelles sources de données à l'avenir (par exemple, bases de données, API).

####  Observateur/Observable (Observer et Subject) :

Permet de notifier différentes parties de l'application lorsque des événements importants se produisent (par exemple, fin d'analyse, évaluation terminée).

####  Monteur (Builder) (KeyboardLayoutBuilder, JsonKeyboardLayoutBuilder, KeyboardLayoutDirector) :

Simplifie la construction d'objets complexes comme les dispositions de clavier.
Permet de gérer différentes sources de construction de manière uniforme.

## Diagramme UML des patrons

Pour une meilleure compréhension, voici un diagramme UML simplifié illustrant l'interaction entre les principales classes et patrons de conception.

```lua
+---------------------+          +---------------------+
|     App             |          |   MenuHandler       |
+---------------------+          +---------------------+
| - menuHandler       |<>--------| - scanner           |
| - frequencyAnalyzer |          +---------------------+
| - keyboardEvaluator |
| - textDisplay       |
+---------------------+
        |
        | Uses
        |
+----------------------+          +---------------------+
| FrequencyAnalyzer    |          | KeyboardEvaluator   |
+----------------------+          +---------------------+
| - dataSource         |          | - jsonfile          |
| - observers          |          | - fileCounter       |
| - allResults         |          |                     |
+----------------------+          +---------------------+
| + execute()          |          | + execute()         |
| + registerObserver() |          | + registerObserver()|
| + notifyObservers()  |          | + notifyObservers() |
+----------------------+          +---------------------+
        |
        | Decorated by
        |
+-----------------------------------+
| LoggingFrequencyAnalyzerDecorator |
+-----------------------------------+
| - wrappedAnalyzer                 |
+-----------------------------------+
| + execute()                       |
| + getResults()                    |
+-----------------------------------+

```
##### Explications :

- App utilise MenuHandler pour gérer les interactions utilisateur.
- App détient des instances de FrequencyAnalyzer, KeyboardEvaluator, et TextDisplay.
- FrequencyAnalyzer et KeyboardEvaluator peuvent enregistrer des observateurs pour recevoir des notifications.
- FrequencyAnalyzer est décoré par LoggingFrequencyAnalyzerDecorator pour ajouter des fonctionnalités de journalisation.

## Structure du Projet
```bash
/src/main/java
│
├── /clavier
│   ├── azerty.json
│   ├── qwerty.json
│   └── ... autres dispositions ...
│
├── /texte
│   ├── java.txt
│   ├── livretJava.txt
│   ├── notes_logique.txt
│   └── ... autres textes ...
│
├── /resultat
│   ├── analyseur.json
│   └── ... autres résultats ...
│
├── /config
│   ├── Analyseur.java
│   └── Evaluateur.java
│
├── /utils
│   ├── ConsoleUtils.java
│   ├── FileCounter.java
│   ├── Jsonfile.java
│   └── Readfile.java
│
├── /services
│   ├── AbstractService.java
│   ├── FrequencyAnalyzer.java
│   ├── KeyboardEvaluator.java
│   ├── KeyboardDisplay.java
│   ├── ResultClearer.java
│   ├── TextDisplay.java
│   ├── MenuHandler.java
│   ├── IFrequencyAnalyzer.java
│   ├── LoggingFrequencyAnalyzerDecorator.java
│   ├── observer
│   │   ├── Observer.java
│   │   ├── Subject.java
│   │   └── LoggerObserver.java
│
└── App.java
```

#### Description des Dossiers Principaux
- `/clavier` : Contient les fichiers JSON des différentes dispositions de clavier.
- `/texte` : Contient les fichiers texte à analyser.
- `/resultat` : Contient les résultats des analyses en format JSON.
- `/config` : Contient les classes de configuration telles que Analyseur et Evaluateur.
- `/utils` : Contient les classes utilitaires pour diverses tâches comme la gestion des fichiers, la lecture de fichiers, etc.
- `/services` : Contient les classes principales qui gèrent les différentes fonctionnalités de l'application, y compris les implémentations des patrons de conception.

## Configuration des Dispositions de Clavier

Les dispositions de clavier sont stockées dans le répertoire `/clavier` sous forme de fichiers JSON. Chaque fichier représente une disposition de clavier avec comme informations sur chaque touche: 
  - la rangée
  - la colonne
  - le doigt utilisé
  - si elle est une touche de doigt de repos (home),
  - si elle nécessite l'utilisation de la touche Shift.

## Options du Menu

1. **Analyseur de Fréquence de Suites de Caractères**
Analyse la fréquence des suites de caractères (monogrammes, bigrammes, trigrammes) dans les fichiers texte sélectionnés.
Pour Définir la fréquence des caractères :
Entrez un nombre entier entre 1 et 4.
- 1 : Monogrammes
- 2 : Bigrammes
- 3 : Trigrammes
- 4 : Monogrammes, Bigrammes et Trigrammes simultanément

2. **Évaluateur de Disposition de Clavier**
Évalue l'efficacité des dispositions de clavier disponibles en fonction des statistiques de fréquence générées par l'analyseur.
Avec comme critère d'evaluation:
      - `inconue` (rien) : le clavier n'a identifier une touche
      - `SFB`  (same finger bigram) : les mouvements à un seul doigt
      - `ciseaux`: mouvements à une main faisant succéder la rangée inférieure et la
rangée supérieure
      - `LSB`  (left shift bigram) : mouvements à extension latérale
      - `alternance` 
      - `roulement`
      - `mauvaise redirection` 
      - `redirection` 
      - `SKS` (same key same finger)
      - `autre_bigramme` 
      - `autre_trigramme` 
      - `tout les doitgs du clavier` (depend du pourcentage)
