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