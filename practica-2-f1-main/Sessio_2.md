Sessió 2
=========

A la darrera sessió vam conèixer alguns conceptes nous relacionats amb les API i com crear diferents punts finals ('endpoints') per crear i modificar dades. Però, com ja heu notat, hem utilitzat el diccionari com a emmagatzematge i aquest tipus d’estructura no ens permet mantenir la persistència de les dades de les nostres modificacions.

En aquesta sessió, afegirem la persistència de dades mitjançant un sistema de base de dades definit i gestionat pels mòduls de Flask, modificarem els nostres mètodes GET, POST, PUT i DELETE per gestionar partits, estadis i equips amb persistència de dades i crearem nous mètodes per gestionar els `Teams` dins d `Matches (Matches.Teams)`. Finalment, també afegirem nous endpoints relacionats amb aquests nous mètodes.

SQLAlchemy: definició de l'estructura de dades mitjançant models
------------------------------------------------

Els models de dades s’utilitzen en diferents aplicacions que gestionen l’emmagatzematge de dades i determinen com es poden organitzar i manipular les dades. Els més populars són els models relacionals, que utilitzen un format basat en taules.

### Configuració SQLAlchemy

Per tal de crear models a la part superior de la nostra base de dades (ORM), utilitzarem un paquet Python anomenat SQLAlchemy. SQLAlchemy ens permet emmagatzemar i recuperar dades mitjançant objectes orientats a objectes amb la seva classe Model. Aquesta classe converteix les dades en objectes Python obtenint un codi compatible amb diferents bases de dades relacionals.
Al nostre projecte, utilitzarem Flask SQLAlchemy, que és un paquet especialitzat relacionat amb SQLAlchemy que ens proporciona detalls de funció per a les aplicacions de Flask.
Per utilitzar aquest nou paquet l’haurem d’instal·lar:

    pip install Flask-SQLAlchemy


I afegiu algunes línies de codi al nostre projecte:

-   Creeu un fitxer `db.py` en el projecte principal (mateixa carpeta que `app.py`)
amb aquest contingut:

    ```python
    from flask_sqlalchemy import SQLAlchemy

            db = SQLAlchemy()
    ```

-   Importeu `db` en el fitxer `app.py`:

    ```python
    from db import db
    ```

-   Afegiu alguns paràmetres de configuració a `app.py` amb `SQLALCHEMY_DATABASE_ URI` per a definir on es guarda les dades (en el nostre  cas usarem SQLite en un fitxer anomenat `data.db` en el projecte principal) i `SQLALCHEMY_TRACK_MODIFICATIONS` posat a False per evitar despeses importants que consumeixen memòria. Probablement, en les noves versions de Flask, ja estigui configurat com a Fals per defecte, però és millor assegurar-ho.


Afegiu aquesta configuració després de
 `app = Flask(__name__)`

```python
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
```

SQLAlchemy llegirà la configuració de la nostra aplicació i es connectarà automàticament a la nostra base de dades.

### Definint TeamsModel

En primer lloc, crearem una carpeta `models` al projecte principal per obtenir tots els fitxers de models que hi ha.

En la carpeta `models` creeu  un arxiu `teams.py` on definirem la classe model `TeamsModel` per a interactuar amb l'API  `teams`. Per a importar la base de dades feu:

```python
from db import db
```

Prenent com a referència l’estructura de dades de la sessió anterior, crearem una taula d’equips amb 3 variables de classe
(`id, name, country`) corresponent a les diferents columnes de la taula.
Definirem l'estructura "TeamsModel" que defineix el nom de la taula, el nom, el tipus i el nombre de columnes, com ara:

```python
class TeamsModel(db.Model):
    __tablename__ = 'teams' #This is table name

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30))
    country = db.Column(db.String(30))
```

Mireu aquesta estructura:

-   `id` es defineix com a  `Integer` i `primary_ key= True`. Això indica que aquest valor s'utilitzarà per enllaçar amb altres taules (en el nostre cas, un enllaç entre "equips" i "partits"). Aquest valor es defineix com a `UNIQUE` per defecte. En cas contrari, no estarem segurs de quin esdeveniment està relacionat amb quin equip.

-   `name` i `country` estan definits com a `String` amb una mida màxima de 30. No és obligatori definir una longitud màxima però es recomana.


Finalment, afegirem alguns paràmetres de configuració més a les nostres columnes:

```python
name = db.Column(db.String(30), unique = True, nullable=False)
country = db.Column(db.String(30), nullable=False)
```

-   `unique = True`: evita tenir dos equips amb el mateix nom a la nostra base de dades

-   `nullable = False`: no permet tenir valors buits en aquesta columna

També podem definir alguns mètodes relacionats amb aquesta taula en funció de les nostres necessitats, però és obligatori definir un mètode `__ init__` per definir com es creen els objectes. Afegiu aquest mètode a TeamsModel:

```python
def __init__(self, name, country):
        self.name = name
        self.country = country
```

Fixeu-vos que no cal inicialitzar l'identificador, SQLAlchemy ho farà quan desem aquest tipus de dades a la base de dades.
### Exercici 1: 

### Definiu MatchModel i CompetitionsModel

### Exercici 2.1: 

1.  Definiu la classe `CompetitionsModel` a `competitions.py` dins de la carpeta `models`, amb la taula anomenada `competitions` amb aquests paràmetres (de moment):

    -   `id` (Integer and primary_key)

    -   `name` (String)

    -   `category` (Enum)

    -   `sport` (Enum)

    `category` i `sport` es defineixen com a `Enum`, que indica que només pot tenir els valors definits en una enumeració. En el nostre cas, les aquestes son:

    ```python
    categories_list = ("Senior","Junior")
    sports_list = ("Volleyball","Football","Basketball","Futsal")
    ```
    I, per tant, definirem el camps `category` i `sport` de la següent manera:

    ```python
    category = db.Column(db.Enum(*categories_list),nullable=False)
    sport = db.Column(db.Enum(*sports_list),nullable=False)
    ```

    Podeu afegir les categories o esports extres que volgueu.
    
2.  Definiu com a no `nullable` els paràmetres que ho necessitin.

3.  Definiu el mètode `__init__`.

4.  Definiu una `UniqueConstraint` per a evitar l'existència d'una competició amb el mateix (`’name’,’category’,’sport’`) afegint-ho a CompetitionsModel:

    ```python
    __table_args__ = (db.UniqueConstraint('name', 'category', 'sport'),)
    ```

### Exercici 2.2: 
    
1.  Definiu la classe `MatchModel` a `match.py` dins de la carpeta `models`, amb la taula anomenada `matches` amb aquests paràmetres (de moment):

    -   `id` (Integer and primary_key)

    -   `date` (DateTime)

    -   `price` (Float)

    Més endevant explicarem com afegir els camps `local`,`visitor` i `competition`.

2.  Definiu tots aquests paràmetres com a no `nullable`.


### Definint relacions entre models

Les relacions entre models a SQLAlchemy són enllaços entre dos o més models que permeten als models referenciar-se automàticament.
Fixeu-vos en el següent diagrama de classes de les nostres dades:


Com podeu veure, tenim dos tipus de relacions:

* "Many-To-Many" o "molts a molts" entre equips i competicions, ja que un equip pot participar en una o més competicions i una competició està present en un o més equips. En SQLAlchemy es tradueix de la següent manera:

```python
teams_in_competitions = db.Table("teams_in_competitions",
                                 db.Column("id", db.Integer, primary_key=True),
                                 db.Column("team_id", db.Integer, db.ForeignKey("teams.id")),
                                 db.Column("competition_id",db.Integer, db.ForeignKey("competition.id")))

class CompetitionsModel(db.Model):

    __tablename__ = 'competition'  # This is table name
    __table_args__ = (db.UniqueConstraint('name', 'category', 'sport'),)

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30), nullable=False)
    category = db.Column(db.Enum(*categories_list),nullable=False)
    sport = db.Column(db.Enum(*sports_list),nullable=False)

    teams = db.relationship("TeamsModel",secondary=teams_in_competitions,backref=db.backref("competition"))
```

* "One-To-Many" o "d'1 a molts" entre un match i una competició, ja que un match sempre formarà part d'una sola competició però aquesta competició pot tenir un o més matches. També tenim present aquesta relació entre TeamsModel i MatchesModel: un match tindrà sempre un equip local i un equip visitant (_Child class_), però un equip està present en un o més matches (_Parent class_). Aquest tipus de relació no necessita una taula secundària de suport com la de `teams_in_competitions_`. En SQLAlchemy es tradueix de la següent manera:

```python
class MatchesModel(db.Model):
    __tablename__ = 'match' #This is table name
    __table_args__ = (db.UniqueConstraint('local_id', 'visitor_id', 'competition_id', 'date'),)

    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.DateTime, nullable=False)
    price = db.Column(db.Float, nullable=False)

    local_id = db.Column(db.Integer, db.ForeignKey("teams.id"), nullable=False)
    visitor_id = db.Column(db.Integer, db.ForeignKey("teams.id"), nullable=False)
    local = db.relationship("TeamsModel",foreign_keys=[local_id])
    visitor = db.relationship("TeamsModel",foreign_keys=[visitor_id])
```

De manera general, una relació "Many-to-one" segueix el següent esquema:

```python
class Parent(Base):
    __tablename__ = 'parent'
    id = Column(Integer, primary_key=True)
    child_id = Column(Integer, ForeignKey('child.id'))
    child = relationship("Child")

class Child(Base):
    __tablename__ = 'child'
    id = Column(Integer, primary_key=True)
```

### Exercici 3:

Definiu, seguint l'esquema anterior, la relació "1-to-Many" entre MatchesModel i CompetitionsModel esmentada. Amb aquestes noves relacions, creeu/modifiqueu les `UniqueConstraint` de cada model. En els mètodes `__init__`, poseu-hi tots els atributs que no siguin relacions o `ForeignKey`. Us podeu guiar del següent esquema:

![image](figures/DiagramaClases.png)


Migracions: creació / actualització de la nostra estructura d’emmagatzematge de dades
---------------------------------------------------------

A la secció anterior hem definit el nostre magatzem de dades mitjançant Models, però per poder utilitzar-lo hem de crear-lo a la base de dades. El podem fer "a mà" al nostre codi, creant taules utilitzant ordres SQL. Però podem fer un pas més amb Flask-Migrate.
Flask-Migrate proporciona una manera de fer front als canvis en l'esquema de la base de dades
usant SQLAlchemy, utilitzant només unes poques ordres a la línia de comandes.

### Configure Flask-Migrate

Com sempre, abans d’utilitzar aquest paquet de Flask l’hem d’instal·lar:

    pip install Flask-Migrate

I afegiu-hi algunes línies a `app.py`:

```python
from flask_migrate import Migrate
```

Assegureu-vos que el vostre fitxer principal s'anomeni "app.py" perquè
flask-migrate està buscant aquest fitxer. A més, s’importen els nostres models
allà:

```python
 from models.teams import TeamsModel
 from models.matches import MatchesModel
 from models.competitions import CompetitionsModel
```

Afegiu `Migration` i la inicialització del ORM SqlAlchemy després de tots els `app.config`:

```python
migrate = Migrate(app, db)
db.init_app(app)
```

A les següents seccions anem a treballar-hi. Assegureu-vos que heu desat totes les modificacions abans d’executar les nostres ordres de migració.

### Executeu migracions

Obriu el terminal mitjançant PyCharm o Terminal a la carpeta principal i executeu aquestes ordres:

    flask db init

Això afegirà una carpeta de migracions a la vostra aplicació. Si voleu fer control de versions de les migracions fetes, cal afegir el contingut d’aquesta carpeta al control de versions juntament amb els altres fitxers font.

A continuació, podeu generar una migració inicial:

    flask db migrate -m "Initial migration".


A continuació, podeu aplicar la migració a la base de dades:

    flask db upgrade

Després, **cada vegada que canvien els models de base de dades**, repetiu els dos passos anteriors amb un missatge diferent. Penseu però que cada vegada que canvieu el model de la base de dades heu de pensar què fareu amb les dades que ja existeixen però que puguin ser incompatibles amb el nou model de dades (per exemple un camp passa ser no null i teniu dades amb aquest camp a null). Les podeu o bé eliminar o bé actualitzar-les amb dades que les facin compatibles amb el nou esquema.  

Per veure totes les ordres disponibles, executeu aquesta ordre:

    flask db --help

### Afegint dades

En aquest moment tenim un emmagatzematge de dades buit a punt per utilitzar. Utilitzarem la línia de comandes de flask (PyCharm o Terminal) per aprendre a afegir-ne, modificar-la i suprimir-ne informació. També és important saber construir la nostra consulta per obtenir la informació desitjada.
Obriu Flask Shell escrivint `flask shell` al terminal o PyCharm
Terminal (assegureu-vos de fer-ho des de la carpeta principal del projecte). Assegureu-vos que no executeu la API al mateix temps.
Importeu el nostre "db", inicialitzeu una aplicació i comproveu alguns paràmetres relacionats amb els nostres models després d'importar-los:

    >>> from db import db

    >>> db
    
    >>> app
   
    >>> from models.teams import TeamsModel

    >>> TeamsModel
    
    >>> from models.show import MatchModel

    >>> MatchModel

Creeu alguns equips, partits i competicions i deseu-los al nostre emmagatzematge de dades. **Nota**: si durant les següents instruccions obteniu algun error d'esquema i no el podeu solucionar, proveu de suprimir el fitxer `data.db` i la carpeta migrations. Després, torneu a executar les instruccions [inicials](###Executeu-migracions).

    >>> local = TeamsModel('Karasuno', 'Japan')
    >>> visitor = TeamsModel('Nekoma','Japan') 
 
    >>> db.session.add(local)
    >>> db.session.add(visitor)
    >>> db.session.commit()
    
    >>> from datetime import datetime
    >>> new_match = MatchesModel(datetime.strptime('2022-07-04', "%Y-%m-%d"),'50.0')
    >>> db.session.add(new_match)
    >>> db.session.commit()

    >>> new_competition = CompetitionsModel("Annual Prefectural Volleyball Tournament","Junior","Volleyball")
    >>> db.session.add(new_competition)
    >>> db.session.commit()

Creeu algunes consultes senzilles (`filter_ by`):

    >>> TeamsModel.query.filter_by(name="Karasuno").first()
    <TeamsModel 2>
    >>> team = TeamsModel.query.filter_by(name="Karasuno").first()
    >>> teams.name, teams.country
    ('Karasuno', 'Japan')
    >>> teams = TeamsModel.query.filter_by(country='Japan').all()
    >>> teams
    [<TeamsModel 1>, <TeamsModel 2>]
    >>> teamss[0].name, teamss[1].name
    ('Karasuno', 'Nekoma')

Modifiqueu un partit i deseu-lo:

    >>> new_match
    <MatchesModel 1>
    >>> new_match.price = 60.0
    >>> db.session.add(new_match)
    >>> db.session.commit()
    >>> match = MatchesModel.query.filter(MatchesModel.price>=60).first()
    >>> match.price, match.date
    (60.0, datetime.datetime(2021, 7, 4, 0, 0))

Creeu les associacions:

    >>> new_competition.teams.append(local)
    >>> new_competition.teams.append(visitor)
    >>> new_competition.match.append(match)
    >>> db.session.add(new_competition)


    >>> new_match.local = local
    >>> new_match.visitor = visitor
    >>> new_match.competition = new_competition
    >>> db.session.add(new_match)

    >>> db.session.commit()

Comproveu que tot i que no hem associat el camp `competition_id` de MatchesModel, SQLAlchemy l'ha establert automàticament al crear la relationship:

    >>> MatchesModel.query.all()[0].competition_id
    1

Filtres avançats:

    >>> match = MatchesModel.query.filter_by(id=1).filter(CompetitionsModel.teams.any(name='Karasuno')).first()
    >>> match.competition.name
    "Annual Prefectural Volleyball Tournament"

Filtres que fan servir diverses taules enllaçades(`join`):

    >>> competition = CompetitionsModel.query.join(CompetitionsModel.teams).filter(TeamsModel.name=="Karasuno").first()
    >>> competition.name
    "Annual Prefectural Volleyball Tournament"

Aquesta consulta es tradueix com "Dona'm la primera competició que trobis on participi l'equip amb nom 'Karasuno'".

Filtres **sense especificar "first()" o "all()"** són només consultes SQL que
SQLAlchemy crea a partir del nostre filtre:

    >>> query = CompetitionsModel.query.join(CompetitionsModel.teams).filter(TeamsModel.name=="Karasuno")
    >>> print(query)

    ``` sql
    SELECT competition.id AS competition_id, competition.name AS competition_name, competition.category AS competition_category, competition.sport AS competition_sport 
    FROM competition JOIN teams_in_competitions AS teams_in_competitions_1 ON competition.id = teams_in_competitions_1.competition_id JOIN teams ON teams.id = teams_in_competitions_1.team_id 
    WHERE teams.name = ?
    ```

Suprimir un equip d'una competició:

    >>> competition.teams.remove(local)
    >>> db.session.add(competition)
    >>> db.session.commit()
    >>> competition = CompetitionsModel.query.join(CompetitionsModel.teams).filter(TeamsModel.name=="Karasuno").first()
    >>> competition.teams
    [<TeamsModel 2>]

Tanqueu la sessió i sortiu de Terminal:

    >>> db.session.close()
    >>> exit()

Si intenteu fer una acció no permesa al nostre emmagatzematge de dades, el shell
retornarà un error que normalment evita aplicar més accions a 
la base de dades. Per resoldre aquest problema, la millor manera és retrocedir
aquesta ordre:

    db.session.rollback()

I assegureu-vos sempre de tancar la sessió amb la base de dades abans de sortir de
Terminal.

### Exercici 4:

Creeu un script Python anomenat `add_data.py` a la carpeta principal del projecte que afegeixi automàticament alguns equips, partits i competicions i les seves relacions a la nostra base de dades. Per a fer-ho, modifiqueu el fitxer `data.py` tenint en compte com heu definit els mètodes `__init__` de cada model. A mode d'ajuda, a continuació podeu veure un esquelet de `add_data.py` incomplet:


```python
from flask import Flask
from flask_sqlalchemy import SQLAlchemy

#TODO: import models here

import data

import random

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)
db.init_app(app)


teams = []
matches = []
competitions = []

for competition in data.competitions:
    competitionModel = CompetitionsModel(name=competition["name"],category=competition["category"],sport=competition["sport"])
    competitions.append(competitionModel)

#TODO: Repeat the process above process for teams and matches!

#Relationships
for match in matches:
    #TODO: Choose randomly two different teams from the teams list
    local = 
    visitor = 
    
    #Assign local and visitor for the current match
    match.local = local
    match.visitor = visitor

    #TODO: Choose a random competition
    random_competition = 
    #Assign the competition to the match
    match.competition = random_competition

    #TODO: Append the match to the competition
    #TODO: Append local and visitor to the competition list of teams 

db.session.add_all(teams)
db.session.add_all(matches)
db.session.add_all(competitions)
db.session.commit()
```

* Recordeu que el paràmetre `date` de MatchesModel només accepta variables de tipus `datetime`.

* Fixeu-vos en el mètode `db.session.add_all(list)` el qual fa només una crida a la base de dades que afegeixi totes les entrades de cop. Després fem `commit()` un cop afegides.

* Executeu el fitxer amb:

    >>> python3 add_data.py

* Podeu comprovar si les dades s'han afegit correctament a la ``flask shell``.

Modifiqueu els nostres recursos anteriors: DEURES
---------------------------------------

1.  Creeu els mètodes `def json(self):` a tots els Models, que retorni els seus continguts en format JSON. Per exemple, per competitions:

	```python
	
	 def json(self):
        return {'id': self.id, 'name': self.name, 'category': self.category, 'sport': self.sport, 'teams':  [teams.json() for teams in self.teams], 'matches': [match.json() for match in self.match]}
	
	```                
    **Nota**: compte amb els bucles infinits! Quan realitzis la funció ``json()`` de matches, aquesta imprimirà el nom de la competició, és a dir, ``competition.name``, ja que si posem ``competition.json()`` tornarem a cridar `matches.json()` etc. etc. etc.

	En el cas de serialitzar el datetime farem servir el isoformat per a que sigui compatible per tothom.
	
	```python
	a_datetime = datetime.datetime.now()
	formatted_datetime = a_datetime.isoformat()
	```
	
	i per passar de String isoformat a datetime:
	
	```python
	import dateutil.parser
	yourdate = dateutil.parser.parse(formatted_datetime)
	```

2.  Creeu mètodes a tots els Models per desar o eliminar de la base de dades.

    ```python
     def save_to_db(self):
        db.session.add(self)
        db.session.commit()

     def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()
    ```
 
	El podem utilitzar com:
	
    ```python
    teams = TeamsModel(...)
    teams.save_to_db()
    ```

3.  Creeu alguns mètodes de classe amb filtres que us puguin ser d'utilitat en els Models, per exemple:

    ```python
    	@classmethod
       def get_by_id(self,id):
                #code here
    ```

   El podem utilitzar com:

    ```python
    teams = TeamsModel.get_by_id(3)
    ```

4.  Modifiqueu els punts finals (endpoints) de la sessió 1 per utilitzar la nova estructura de dades:

    ```python
    		api.add_resource(Teams, '/team/<int:id>', '/team')
            api.add_resource(TeamsList, '/teams')

            api.add_resource(Competitions, '/competition/<int:id>', '/competition')
            api.add_resource(CompetitionsList, '/competitions')
            
            api.add_resource(Matches, '/match/<int:id>', '/match')
            api.add_resource(MatchesList, '/matches')
    ```

Ara que tenim la nostre base de dades creada i llesta, hem de suprimir tota importació relacionada amb ``data.py``. Ara, tota la gestió de crear, modificar, eliminar o retornar un objecte (model) de la nostre base de dades es farà a travès de les seves classes ``TeamsModel``, ``MatchesModel`` i ``CompetitionsModel``. Seguirem fent ús de ``reqparse``, però ara les sol·licituds POST i PUT sempre tindran tots els paràmetres. Modifiqueu "reqparse" per indicar que tots els paràmetres són necessaris i afegiu informació als paràmetres d’ajuda:


```python
    parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
```

Per exemple, el següent codi mostra com puc crear matches passant o no l'identificador d'equip local o no:

```python
    parser.add_argument('local_id', type=int, required=False)
    parser.add_argument('visitor_id', type=int, required=False)
    data = parser.parse_args()

    match = MatchesModel(datetime.strptime(data["date"], "%Y-%m-%d"), data["price"])
    if data["visitor_id"] and data["local_id"]:
        # Create add_teams() method in MatchesModel
    try:
        ...
```

Tingueu en compte les definicions ("unique = True" i "UniqueConstraint") per evitar errors abans d’intentar desar-lo a la base de dades.

Afegiu `try-catch` quan intenteu desar a la base de dades per evitar errors interns:

```python
try:
    new_teams.save_to_db()
except:
    return {"message": "An error occurred inserting the teams."}, 500
```

5.  Creeu aquests nous punts finals i recursos:

    ```python
    api.add_resource(CompetitionTeamsList, '/competition/<int:id>/teams')
    api.add_resource(CompetitionMatch, '/competition/<int:id>/match',
                                    '/competition/<int:id>/match/<int:id>')

    api.add_resource(TeamMatchesList, '/team/<int:id>/matches')
    api.add_resource(MatchTeamsList, '/match/<int:id>/teams')
    
    ```

    **Nota**: En tots els següents endpoints s'obvia que sempre es comprovarà que els id's dels diferents models existeixi a la base de dades, i que en cas contrari es retorna el missatge d'error corresponent. 

    **CompetitionTeamsList** :

    -   get: retornar tots els equips d'una competició, donat el seu identificador

    **CompetitionMatch**:

    -   get: retorna, si existeix, el match corresponent a l'id de match passat (en el cas del GET és obligatori) si està dins de la competició corresponent a l'id de competició passat.

    -   post: té dos comportaments. 
        1. Si s'especifica id del match:
            * Afegeix el match dintre de la competició, comprovant abans si no està afegit ja. En ambdós casos, retorna la informació de la competició.
        2. Si no s'especifica l'id del match:
            * Crea el match amb la informació del JSON de la request i el guarda a la base de dades, comprovant abans que el match no existeixi. 
            * Afegeix el match acabat de crear dins de la competició. Retorna la informació de la competició.

    -   delete: elimina el match concret de la competició especificada. Retorna la informació de la competició

    **TeamMatchesList**:

    -   get: torna tots els partits d’un equip, donat el seu identificador

    **MatchTeamsList**:
    
    - get: torna tots els equips d'un match, donat el seu identificador

    Codis d'errors del protocol HTTP:
    ![image](figures/errors.png)

6.  Proveu i comproveu tots els vostres punts finals mitjançant sol·licituds o Postman, i
    assegureu-vos que la vostra API funcioni correctament.