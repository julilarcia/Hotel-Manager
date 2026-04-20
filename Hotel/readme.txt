***

# Hotel - Aplikacja do Zarządzania Hotelem

Interaktywna aplikacja konsolowa (CLI) do zarządzania hotelem napisana w Javie. Umożliwia obsługę zameldowania, wymeldowania, przeglądanie stanów pokoi oraz zapis i wczytywanie danych w formacie CSV.

## Główne funkcjonalności

| Komenda | Opis |
| :-- | :-- |
| `checkin <roomNumber> <firstName> <lastName> <days> [date] [notes]` | Zameldowanie gościa w pokoju |
| `checkout <roomNumber>` | Wymeldowanie gościa i wyliczenie należności |
| `view <roomNumber>` | Wyświetlenie informacji o pokoju |
| `list` | Lista wszystkich pokojów |
| `listAvailable` | Lista dostępnych pokojów |
| `prices` | Ceny wszystkich pokojów |
| `save` | Zapis aktualnego stanu do pliku |
| `exit` | Zakończenie programu |

## Wymagania techniczne

- Java 17 (JDK 17+)
- Maven 3.6+
- Terminal z obsługą ANSI (opcjonalnie)
- Logback (opcjonalnie w runtime, zadeklarowany w pom.xml)


## Szybki start

### Budowanie projektu

```bash
mvn clean package
```


### Uruchomienie

```bash
java -jar main/target/main-1.0-jar-with-dependencies.jar
```


### Inicjalizacja danych

- Aplikacja sprawdza istnienie `hotel_saved.csv`.
- Jeśli plik istnieje, pyta o wczytanie go.
- Jeśli nie, wczytuje dane z `hotel.csv`.


## Struktura projektu

```
Hotel/                                    (root, pakietowanie pom)
├─ pom.xml                               (parent POM)
├─ README.md                             (dokumentacja)
├─ hotel.csv                             (plik startowy z danymi)
├─ hotel_saved.csv                       (plik zapisu stanu, dynamiczny)
│
├─ utils/                                (moduł pomocniczy)
│  ├─ pom.xml
│  └─ src/
│     ├─ main/java/pl/edu/agh/kis/pz1/hotel/utils/
│     │  ├─ MyMap.java                   (generyczna struktura danych)
│     │  └─ Map.java                     (interfejs Map)
│     └─ test/java/pl/edu/agh/kis/pz1/hotel/utils/
│        └─ MyMapTest.java               (testy jednostkowe)
│
└─ main/                                 (moduł aplikacji)
   ├─ pom.xml
   └─ src/
      ├─ main/
      │  ├─ java/pl/edu/agh/kis/pz1/
      │  │  ├─ app/
      │  │  │  └─ HotelManagerApp.java   (główna klasa, REPL)
      │  │  ├─ command/
      │  │  │  └─ CommandProcessor.java  (przetwarzanie komend)
      │  │  ├─ io/
      │  │  │  ├─ HotelDataLoader.java   (wczytywanie CSV)
      │  │  │  └─ HotelDataSaver.java    (zapis CSV)
      │  │  ├─ model/
      │  │  │  ├─ Hotel.java             (klasa hotel)
      │  │  │  ├─ Room.java              (klasa pokój)
      │  │  │  └─ Guest.java             (klasa gość)
      │  │  └─ service/
      │  │     └─ HotelService.java      (logika biznesowa)
      │  └─ resources/
      │     └─ logback.xml               (konfiguracja loggera)
      └─ test/
         └─ java/pl/edu/agh/kis/pz1/
            ├─ io/
            │  └─ HotelDataLoaderTest.java
            |  |__HotelDataSaverTest.java
            ├─ model/
            │  ├─ GuestTest.java
            │  ├─ RoomTest.java
            │  └─ HotelTest.java
            └─ service/
               └─ HotelServiceTest.java
```


## Kluczowe komponenty

### Model danych (model/)

- **Guest** - reprezentuje gościa (imię, nazwisko, opcjonalnie email, telefon, notatki)
- **Room** - reprezentuje pokój (numer, opis, cena, pojemność, goście, daty, zajętość, notatki)
- **Hotel** - kolekcja pokoi w `MyMap<Integer, Room>`


### Logika biznesowa (service/)

- **HotelService** - zarządza operacjami checkin, checkout, przeglądaniem list i cen pokoi


### Obsługa plików CSV (io/)

- **HotelDataLoader** - wczytuje dane z plików CSV zgodnie z formatem
- **HotelDataSaver** - zapisuje aktualny stan hotelu do pliku CSV


### Struktura danych (utils/)

- **MyMap** - własna generyczna mapa klucz-wartość z metodami `put`, `get`, `remove`, `contains`, `keys`


### Przetwarzanie komend (command/)

- **CommandProcessor** - obsługuje REPL, waliduje i wykonuje komendy użytkownika


## Format danych CSV

### Wejściowy

```
roomNumber,description,price,capacity,guests,checkinDate,notes
101,jedynka,150.0,1,Kupidyna K.,2025-11-01,
102,dwójka,200.0,2,,,
103,pokój 3,300.0,4,Syzyf S.|Fidor F.,2025-11-03,
```

- Goście są rozdzieleni znakiem `|`, format: `Imię Nazwisko`
- Data w formacie ISO `yyyy-MM-dd`; jeśli pusta, jest używana data bieżąca
- Pole `notes` opcjonalne


## Uruchamianie testów

```bash
mvn test
```

Testy obejmują moduły modelu, ładowanie danych, logikę biznesową oraz strukturę danych MyMap.

## Przykład sesji

```
> checkin 101 Jan Kowalski 3
Checked in successfully.

> view 101
room 101 (jedynka), price: 150.0, capacity: 1, occupied: true
  guests: Jan Kowalski;
  check-in: 2025-11-18
  planned checkout: 2025-11-21

> listAvailable
[Room 102, Room 103, ...]

> save
Hotel saved to a file: hotel_saved.csv

> exit
byebye~. u MUST come back!
```


## Ważne uwagi

- Opłata za pobyt liczona: liczba nocy * cena za noc (liczba nocy to różnica między datą bieżącą a datą zameldowania)
- Obowiązkowa walidacja komend z jasnymi komunikatami błędów (`invalid room number`, `check-in failed`, itp.)
- Komunikaty użytkownika wyświetlane przez `System.out`, logi diagnostyczne przez SLF4J + Logback


## Możliwe rozszerzenia

- Obsługa zaawansowanych formatów CSV (np. Apache Commons CSV)
- Baza danych zamiast plików CSV
- Interfejs graficzny (GUI)
- System autoryzacji personelu
- Rozliczanie VAT w opłatach

***

