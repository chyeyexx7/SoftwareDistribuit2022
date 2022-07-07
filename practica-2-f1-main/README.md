---
author:
- Blai Ras Jimenez, David Martinez Carpena, Eloi Puertas
date: Març 2022
title: Pràctica 2 - Flask and Vue application (Software distribuït)
---

Introducció
============

En aquesta pràctica anem a desenvolupar una pàgina web capaç de mostrar 
informació sobre esdeveniments esportius i reservar-ne les entrades.
Però primer de tot anem a desenvolupar una REST-API capaç de proporcionar-nos la informació necessària. També desenvoluparan les funcions relacionades amb la gestió d'aquestes dades com
afegir, eliminar o modificar les dades emmagatzemades.

Amb aquest propòsit utilitzarem la llibreria Python anomenada Flask amb el paquet per a fer  API anomenada Flask-RESTful. Flask
és un marc d'aplicacions web lleugeres (Framework) que gràcies a la seva flexibilitat i
versatilitat s'ha convertit en un dels Frameworks més populars de Python.

IMPORTANT
---------

Aquest exercici guiat suposa que ja teniu alguna versió de Python 3
instal·lada i esteu familiaritzats amb la instal·lació de paquets mitjançant pip.

Tingueu en compte que el pip command mostrat en aquest tutorial correspondrà a pip3 o
pip3.7 segons quantes versions diferents de python tingueu
instal·lat al vostre ordinador.

Es recomana altament l'ús de la IDE [PyCharm](https://www.jetbrains.com/pycharm/) per al desenvolupament d'aquesta pràctica

Enviament dels Exercicis
------------------------
Es treballarà de la mateixa manera que a la Pràctica 1. Els exercicis i deures s'enviaran com a Pull Requests al repositori del Github Classroom. Les respostes a les preguntes 
s'enviaran com a commentaris en el Pull Requests. S'ha de fer Peer Review dels Pull Requests per part de l'altre membre del grup.

Avaluació pràctica 2.
---------------------------
- 50% Fer els enviaments de deures i exercicis setmanals proposats.
- 50% Lliurament final: 20% Sessió de Test, 80% codi final.

Notació usada en aquest tutorial: 
-------------------------------

 Ordres per introduir a Terminal o trossos de codi Python:

    >>>commands to introduce to Python console

```python
# Piece of code corresponding to some python file. 
from flask import *
```

Sessió 1
=========
- 30 de març [Sessió 1](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_1.md)

Sessió 2
=========
- 20 d'abril [Sessió 2](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_2.md)

Sessió 3
=========
- 27 d'abril [Sessió 3](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_3.md)

Sessió 4
=========
- 4 de Maig [Sessió 4](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_4.md)

Sessió 5
=========
- 11 de Maig [Sessió 5](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_5.md)

Sessió 6
=========
- 18 de Maig [Sessió 6](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_6.md)

Sessió 7
=========
- 18 de Maig [Sessió 7](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_7.md)

Sessió de Test
=========
- 25 de Maig [Sessió Test](https://github.com/SoftwareDistribuitUB-2022/Practica2/blob/main/Sessio_Test.md)
