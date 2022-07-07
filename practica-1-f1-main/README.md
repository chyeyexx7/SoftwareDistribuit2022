---
author:
- Blai Ras, David Martinez Carpena, Eloi Puertas Prats
date: Febrer 2022
title: Pràctica 1 - Client Servidor (Software distribüit)
---

**Recordeu que s'ha de fer el [desenvolupament de les pràctiques](Desenvolupament_de_les_practiques.md) mitjançant els Pull Requests!!!**



Objectiu
========

L'objectiu docent de la pràctica és aprendre a utilitzar els mecanismes de programació Client/Servidor en JAVA. Concretament és necessari que aprengueu com programar amb:

-   Sockets amb JAVA (utilitzant l'API Socket de Java.net)

-   Servidor multi-petició amb threads (JAVA)

Tasques a realitzar
===================

-   El client ha de tenir un mode manual (menú per pantalla) i un mode
    automàtic (juga automàticament segons els paràmetres introduïts)

-   S'ha de fer una implementació del servidor multi-thread que
    serveixi per a que la *màquina* pugui jugar múltiples partides simultànees. 

-   El servidor ha d'escriure un log de l'interacció amb el client a
    fitxer seguint el format especificat.

-   Fer proves de robustesa i d'estrès del sistema.

-   Realitzar codi, Junit, JavaDoc, dossier amb Diagrames i
    autoevaluació de la pràctica.

Calendari
=========
| Data Sessió | Planificació orientativa                                                    | Data límit Peer Review | Puntuació del Review |
|-------------|-----------------------------------------------------------------------------|------------------------|----------------------|
| 16/02/2022  | a) Creació de grups. b) Preparació del GitHub. c) Realiztizació de la prac0 | -                      | 0                    |
| 23/02/2022  | Disseny del sistema distribuït. Implementació Protocol.                     | 02-03/03               | 1                    |
| 02/03/2022  | Implementació Client.                                                       | 09-10/03               | 1                    |
| 09/03/2022  | Implementació Servidor.                                                     | 16-17/03               | 1                    |
| 16/03/2022  | Implementació Servidor amb Multithread                                      | 23-24/03               | 1                    |
| 23/03/2022  | Sessió de Test creuat. Tasca Taller.                                        | -                      | 0                    |
| 29/03/2022  | Entrega codi, memòria i log execució al CV. Tasca Fitxer                    |                        |                      |

Notes importants
================

-   No es demana que s'implementi cap interfície gràfica.

-   En mode manual l'usuari ha de poder jugar tantes partides com
    vulgui. 

-   S'haurà de poder sortir en qualsevol moment de la partida, tallant
    la comunicació amb el servidor. 
-  Es podrà fer el Peer Review fins al final de la sessió de pràctiques marcada en el Data límit Peer Eeview
    
-   Feu un bon ús de la programació Orientada a Objectes i de la metodologia Test-Driven Development. Es penalitzarà si no es fa un bon ús d'interfícies JAVA, separació de responsabilitats i creació de tests.

Notes sobre el Disseny
======================

-   Recordeu que s'han de dissenyar dues aplicacions, **Client** i **Servidor**. Poden tenir classes en comú, per exemple *ComUtils*.

-   La classe *ComUtils* l'heu d'extendre amb els **vostres mètodes** per a seguir **fil per randa** el protocol. Si hi ha mètodes que no els necessiteu, els podeu esborrar.

-   No podeu usar classes de Java per a serialitzar els vostres
    objectes, ja que no seguiran el protocol demanat. **Useu les
    primitives del ComUtils sempre**.

-   **Feu servir JUnit** per comprovar el protocol, per exemple, si s'ha
    enviat una comanda, espero rebre'n unes de determinades. Penseu que
    sempre podríeu rebre un missatge d'error.

-   Per a guardar les comandes d'entrada és una bona pràctica usar un
    HashMap per cada opció entrada amb el seu valor i consultar aquest diccionari quan sigui necessari.

-   El Client en mode automàtic pot jugar de forma totalment aleatòria, però podeu fer servir alogorismes una mica més intel·ligents si voleu.


Execució
========

La execució s'ha de fer des de mode consola obligatòriament. El nom del jar de servidor ha de ser server.jar i el de client client.jar. L'execució es farà amb aquests paràmetres:

-   En el servidor s'especifica l'opció port (-p) on s'especificarà el
    port d'escolta.

-   En el client s'especifica l'opció maquina servidora (-s) on
    s'especificarà la IP del servidor i l'opció port (-p) on
    s'especificarà el port d'escolta del servidor. També es pot especificar l'opció interactive (-i) igual a:

    -   0 vol dir que el joc s'executa en mode manual.

    -   1 s'executarà en mode automàtic prenent decisions.

-   Si no s'especifica l'opció -i, el mode per defecte és el manual. 
- L'opció -h mosta l'ajuda dús de les aplicacions:

```
    servidor> java -jar server.jar -h 
    Us: java -jar server.jar -p <port> 
```
```
    client> java -jar client.jar -h
    Us: java -jar client -s <maquina_servidora> -p <port>  [-i 0|1]
```


Sortida demanada
================

-   El fitxer de log ha de ser la versió textual del que s'està enviant pel socket.

-   Només cal guardar el fitxer de log del Servidor. Heu de guardar
    només el contingut de la comunicació per socket, tant del que es rep
    com el que s'està enviant. En cas de que hi hagi un error també heu
    de guardar el missatge que s'enviï o que es rebi pel socket.

-   El nom del fitxer de log l'heu de construir de la següent forma:

-   \"ServerTest\"+Thread.currentThread().getName()+\".log\"

-   **Sobretot** feu una carpeta src per cada aplicació on les classes
    principals es diguin *Client.java* i *ServerTest.java *respectivament.

Entregues
=========

-   Actualitzar codi a Github mitjançant Pull Requests amb Peer Code Review.
-   Sessió de Test obligatòria (Mínim un component de la
    parella).

-   A CampusVirtual: 29/03/2022 23.59h.

Avaluació
=========

-   En cas de que el codi **no compleixi les especificacions determinades**
    o **no segueixi estíctament el protocol acordat**, la pràctica estarà **SUSPESA**. Per tal d'evitar aquesta circumstància es demanarà fer un Test creuat per a que comproveu la validesa de la vostra solució. 
     
-    Totes les entregues s'executaran de forma automàtica contra els nostres
    servidors i clients amb diferents jocs de proves.
    
-    Es recomana que feu els vostres propis jocs de proves per a provar el vostre
    codi amb els demés en la sessió de test.

-   En cas de que la pràctica funcioni de forma correcta la **nota individual** de cada alumne es ponderarà de la següent forma:

    - 80% Codi:

    	-   50% Review del Codi que s'ha fet al company

    	-   50% Codi (Sense bugs, Codi elegant) + Tests + Memòria i diagrames.
    
    - 20% Peer Testing sessió de test


WORDLE
=================

El Wordle és un joc de paraules en línia gratuït inventat l'any 2021 per Josh Wardle. Gràcies a la seva popularitat a Twitter, el joc va tenir un èxit immediat en la seva versió en anglès. Ràpidament es va oferir en altres idiomes i es va imitar en altres versions.

L'objectiu del joc és endevinar una paraula concreta de cinc lletres en un màxim de sis intents, escrivint lletres en una pantalla de sis línies de cinc caselles cadascuna. El jugador escriu a la primera línia una paraula de cinc lletres de la seva elecció i introdueix la seva proposta. Després de cada proposició, les lletres apareixen en color: el fons gris representa les lletres que no estan a la paraula cercada, el fons groc representa les lletres que es troben en altres llocs de la paraula i el fons verd representa les lletres que estan al lloc correcte en la paraula a trobar.[[https://ca.wikipedia.org/wiki/Wordle]](https://ca.wikipedia.org/wiki/Wordle).

Aquest joc és una adaptació del famós joc Mastermind [[https://ca.wikipedia.org/wiki/Mastermind]](https://ca.wikipedia.org/wiki/Mastermind), on en comptes de fer servir codis numèrics es fan servir paraules vàlides d'un diccionari. 

Missatges
=========
El client (c) i el servidor (s) suporta  tipus de missatges amb els
següents codis d'operacions:

| Message | Code | Direcció|
|---------|-------|--------|
 | HELLO   |1| C-S|
 | READY   |2| S-C|
 | PLAY    |3| C-S|
 | ADMIT   |4| S-C|
 | WORD    |5| C-S|
 | RESULT  |6| S-C|
 | STATS   |7| S-C|
 | ERROR   |8| S-C|


La capçalera d'un missatge conté el codi d'operació associat amb
aquest paquet i els paràmetres necessaris. En les capçaleres dels missages que es detallen a continuació els camps de tipus **string** representen cadenes de bytes codificats en Extended ASCII en format de xarxa (Big Endian). Així mateix, els camps amb tipus d'un o diversos bytes, aquests **bytes** són bytes en format de xarxa (Big Endian). 

-   El paquet **HELLO** (codi d'operació 1) té el format que es mostra en la Figura 1, on *id* és un int32 bytes en format xarxa i on *Name* és el nom del jugador com a string (string representa una cadena de bytes codificats en Extended ASCII en format de xarxa (Big Endian) acabat amb un últim byte 0 que és un byte en format de xarxa (Big Endian)).

                                 1 byte       int32         string   1 byte     
                                -------------------------------------------
                                | Opcode |    SessionId  |  Name    |  0  |
                                -------------------------------------------
                                    Figura 1: Missatge HELLO

 
- El paquet **READY** (codi operació 2) té el format que es mostra en la Figura 2, on *id* és un int32 bytes en format xarxa. 

                                 1 byte    int32       
                                ----------------------------------
                                | Opcode |  SessionId    | 
                                ----------------------------------
       
                                    Figura 2: Missatge READY

- El paquet **PLAY** (codi operació 3) té el format que es mostra en la Figura 3, on *id* és un int32 bytes en format xarxa. 
   
                                  1 byte    int32       
                                ----------------------------------
                                | Opcode |  SessionId | 
                                ----------------------------------
                                    Figura 3: Missatge PLAY



- El paquet **ADMIT** (codi operació 4) té el format que es mostra en la Figura 4, on *bool* és un byte en format xarxa on 0 vol dir no admès i 1 vol dir admès. 

                                  1 byte    1 byte       
                                ----------------------------------
                                | Opcode |  bool    | 
                                ----------------------------------
                                    Figura 4: Missatge ADMIT


- El paquet **WORD** (codi operació 5) té el format que es mostra en la Figura 5, on *Word* és una paraula com a string (string representa una cadena de bytes codificats en Extended ASCII en format de xarxa (Big Endian) de longitud 5.


                                  1 byte    5 byte       
                                ------------------------
                                | Opcode |  Word    | 
                                ------------------------
                                    Figura 5: Missatge WORD


- El paquet **RESULT** (codi operació 6) té el format que es mostra en la Figura 6, on *Result* és un codi com a string (string representa una cadena de bytes codificats en Extended ASCII en format de xarxa (Big Endian) de longitud 5.


	                              1 byte    5 byte       
                                ------------------------
                                | Opcode |  Result    | 
                                ------------------------
                                    Figura 6: Missatge RESULT
- El paquet **STATS** (codi operació 7) té el format que es mostra en la Figura 7, on *JSON* és un JSON vàlid expresat com a string (string representa una cadena de bytes codificats en Extended ASCII en format de xarxa (Big Endian) acabat amb un últim byte 0 que és un byte en format de xarxa (Big Endian)).
  

	                               1 byte   string   1 byte
                                ---------------------------
                                | Opcode |  JSON    | 0
                                ----------------------------
                                    Figura 7: Missatge STATS

	Format del JSON Stats expresat com a string:
	
		
		{
		  "Stats": {
		    "Jugades": 1,
		    "Èxits %": 100,
		    "Ratxa Actual": 1,
		    "Ratxa Màxima": 1,
		    "Victòries":
		      {
		        "1": 0,
		        "2": 0,
		        "3": 1,
		        "4": 0,
		        "5": 0,
		        "6": 0
		      }
		  }
		}


- El paquet **ERROR** (codi operació 8) té el format que es mostra en la Figura 8, on *ErrCode* és és un byte en format xarxa  i on *Msg* és un codi com a string (string representa una cadena de bytes codificats en Extended ASCII en format de xarxa (Big Endian) acabat amb un últim byte 0 que és un byte en format de xarxa (Big Endian)).
  

								1 byte    1 byte      string  1 byte
                                -------------------------------------
                                | Opcode |  ErrCode    | Msg | 0
                                -------------------------------------
                                    Figura 8: Missatge ERROR



                 
Els possibles missatges d'error són:

- ERRCODE: 1, MSG: CARÀCTER NO RECONEGUT
- ERRCODE: 2, MSG: MISSATGE DESCONEGUT
- ERRCODE: 3, MSG: MISSATGE FORA DE PROTOCOL
- ERRCODE: 4, MSG: INICI DE SESSIÓ INCORRECTE
- ERRCODE: 5, MSG: PARAULA DESCONEGUDA
- ERRCODE: 6, MSG: MISSATGE MAL FORMAT
- ERRCODE: 99, MSG: ERROR DESCONEGUT

Protocol
========
La partida la inicia el client amb una comanda **HELLO**. Si es vol continuar una sessió anterior es posa l'int32 associat a la sessió anterior i el nom del jugador que havia usat. En cas que es vulgui començar una sessió nova és passarà l'ID de sessió igual a 0 i el nom del jugador

C -----HELLO (SessionID, name)-----> S

A continuació el servidor contesta amb un **READY**, passant un ID de sessió creat de nou si és una sessió nova. En cas de continuar una sessió es comprovarà que el nom i la sessió s'aparellin correctament i es tornarà a enviar el mateix ID de sessió. En cas contrari s'enviarà un **ERROR** d'*Inici de Sessió Incorrecte*.  

C <------READY (SessionID) --------- S

A continuació el client pot començar la partida amb un **PLAY**, passant l'ID de sessió rebut anteriorment. 

C -----PLAY (SessionID)-----> S

El Servidor comprovarà que aquest SessionID sigui el mateix que es va enviar anteriorment i contestarà amb un **ADMIT** juntament amb un 1 si és correcte o amb un 0 si no ho és. El servidor triarà una paraula vàlida de 5 lletres aleatòria en català. Per fer això podeu usar el següent [diccionari](DISC2-LP-WORDLE.txt) català del Scrable amb totes les paraules de 5 lletres. El caràcter del punt volat ha estat eliminat, per tant les paraules amb ela geminada s'escriuen igual que amb ella. La paraula serà diferent per cada partida i client connectat.
 
C <------ADMIT (bool) --------- S

A continuació el Client enviarà la primera **WORD**, que serà una paraula formada per 5 lletres i en català.

C -----WORD (Word)-----> S

El Servidor comprovarà que la paraula rebuda sigui vàlida.  En cas que la paraula no existeixi es retornarà un **ERROR** de *Paraula Desconeguda*. En cas contrari, s'enviarà un **RESULT** amb un codi de 5 caràcters on **\*** significa que la lletra no es troba en la paraula, **?** que significa que la lletra es troba en la paraula però no en la posició indicada i **^** signifca que la lletra es troba en la posició correcte. En cas que hi hagin lletres repetides en la paraula enviada i només una estigui en la paraual solució però en diferent posició, s'enviarà un ? en qualsevol de les dues posicions i en l'altra es posarà un \*, volent dir que només hi ha una instància d'aquella lletra però en una posició diferent d'on es troben en la paraula enviada. Per exemple: si la paraula enviada pel client era *CALAR* el resultat \*?\*\*^ significa que la C, una A i L no estan a la paraula del servidor, La R està bé i una A està en la paraula però no en cap de les posicions on hi ha A. 

C <------ RESULT (Result) --------- S

A partir d'aquí el client podrà anar enviant WORDs fins a que encerti la paraula o arribi al 6è intent. 
    
Quan el client encerti la paraula, el servidor enviarà després del **RESULT ^^^^^** un  **STATS**. Si és el 6è intent i el client encara no ha encertat la paraula, el servidor enviarà el darrer **RESULT** seguit del **WORD** amb la paraula correcta i després els **STATS** actualitzats.

A Stats es va guardant les estadístiques sobre les partides del jugador durant la sessió actual. El format de les estadístiques és un JSON vàlid, on els camps tenen el següent signficat:

- Jugades: Nombre de partides fetes en la sessió actual.
- Èxits %: Tan per cent de partides guanyades en la sessió actual.
- Ratxa Actual: Quantes partides guanyades consecutives es porten.
- Ratxa Màxima: Nombre màxim de partides guanyades consecutives.
- Victòries: Quantes partides s'han guanyat amb 1, 2, 3, 4, 5 i 6 intents.


		{
		  "Stats": {
		    "Jugades": 1,
		    "Èxits %": 100,
		    "Ratxa Actual": 1,
		    "Ratxa Màxima": 1,
		    "Victòries":
		      {
		        "1": 0,
		        "2": 0,
		        "3": 1,
		        "4": 0,
		        "5": 0,
		        "6": 0
		      }
		  }
		}


Podeu fer servir [JSON-simple](https://code.google.com/archive/p/json-simple/) per validar un JSON en format string [especificació JSON](https://www.ietf.org/rfc/rfc4627.txt). Si el JSON està mal format s'enviarà un **ERROR** de *Missatge Mal Format*.

C <------ STATS (Stats) --------- S

Els missatges d'error es podran enviar en qualsevol moment que el Servidor detecti alguna situació estranya durant l'execució de la partida. Quan el Client rebi un error l'haurà de mostrar per pantalla. El servidor davant dels errors no prendrà cap decisió, simplement es quedarà en l'estat actual esperant un nou missatge del client. El client segons sigui l'error pot o bé tornar a enviar un missatge o tancar la connexió amb el servidor.

C <------ ERROR (ErrCode,Msg) --------- S


Un cop s'hagi acabat una partida, es podrà tornar a jugar una nova partida en la mateixa sessió, per fer això caldrà enviar el missatge **PLAY** 

C -----PLAY (SessionID)-----> S

El client pot acabar quan vulgui les partides, només cal que es desconnecti. 

   
Exemple partida
===============

Exemple de partida. Els espais s'han posat per clairificar els missatges, però en la trama no hi són.


	C- [TCP Connect]
	S- [TCP Accept]
	
	HELLO  C -------1 0 Eloi 0 --------> S
	READY  C <------2 21293212 --------- S
	PLAY   C -------3 21293212 --------> S
	ADMIT  C <------4 1 ---------------- S
	WORD   C -------5 PINSO -----------> S
	RESULT C <------6 ***** ------------ S
	WORD   C -------5 MELER -----------> S
	RESULT C <------6 *^^^^ ------------ S
	WORD   C -------5 TELER -----------> S
	RESULT C <------6 ^^^^^ ------------ S
	STATS  C <------7 {
						  "Stats": {
						    "Jugades": 1,
						    "Èxits %": 100,
						    "Ratxa Actual": 1,
						    "Ratxa Màxima": 1,
						    "Victòries":
						      {
						        "1": 0,
						        "2": 0,
						        "3": 1,
						        "4": 0,
						        "5": 0,
						        "6": 0
						      }
						  }
						} ------------- S
	PLAY   C -------3 21293212 --------> S
	ADMIT  C <------4 1 ---------------- S
	WORD   C -------5 TELER -----------> S
	RESULT C <------6 ??*** ------------ S
	WORD   C -------5 TELER -----------> S
	RESULT C <------6 ??*** ------------ S
	WORD   C -------5 TELER -----------> S
	RESULT C <------6 ??*** ------------ S
	WORD   C -------5 TELER -----------> S
	RESULT C <------6 ??*** ------------ S
	WORD   C -------5 TELER -----------> S
	RESULT C <------6 ??*** ------------ S
	WORD   C -------5 POTES -----------> S
	RESULT C <------6 ????? ------------ S
	WORD   C <------5 ESPOT ------------ S
	STATS  C <------7 {
						  "Stats": {
						    "Jugades": 2,
						    "Èxits %": 50,
						    "Ratxa Actual": 0,
						    "Ratxa Màxima": 1,
						    "Victòries":
						      {
						        "1": 0,
						        "2": 0,
						        "3": 1,
						        "4": 0,
						        "5": 0,
						        "6": 0
						      }
						  }
						} ------------- S
	
	C- [conexion closed]
	S- [conexion closed]


