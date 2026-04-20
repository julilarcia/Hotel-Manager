# Hotel

## 1. Opis Projektu

**Hotel** to interaktywna aplikacja konsolowa (CLI) do zarządzania hotelem, napisana w języku Java. System umożliwia przeprowadzenie operacji związanych z zarządzaniem pokojami hotelowymi, takich jak checkin, checkout, przeglądanie dostępności pokojów oraz operacje na danych hotelowych.

Projekt demonstruje fundamentalne koncepcje programowania obiektowego (OOP), implementację testów jednostkowych oraz autorską strukturę danych generyczną `MyMap`. Dodatkową zaletą aplikacji jest obsługa wczytywania i zapisywania konfiguracji hotelowej z/do plików CSV.

## 2. Główne Funkcjonalności

Aplikacja oferuje następujące komendy dostępne w interfejsie REPL:

| Komenda | Opis |
|--------|------|
| `checkin <roomNumber> <firstName> <lastName> <days>` | Zameldowanie gościa w pokoju na określoną liczbę dni |
| `checkout <roomNumber>` | Wymeldowanie gościa z pokoju i wyliczenie należności |
| `view <roomNumber>` | Wyświetlenie szczegółowych informacji o pokoju |
| `list` | Wylistowanie wszystkich pokojów w hotelu |
| `listAvailable` | Wylistowanie dostępnych (wolnych) pokojów |
| `prices` | Wyświetlenie listy cen wszystkich pokojów |
| `save` | Zapisanie aktualnego stanu hotelu do pliku `hotel_saved.csv` |
| `exit` | Zakończenie programu |

## 3. Wymagania Techniczne

Do uruchomienia projektu niezbędne są:

- **Java 17** (JDK 17 lub wyżej)
- **Maven 3.6+** do budowania projektu
- **Terminal obsługujący ANSI** (opcjonalnie, dla kolorów w interfejsie)
- **Logback** (opcjonalnie w runtime — już zadeklarowany w `pom.xml`)

## 4. Szybki Start

### 4.1 Budowanie Projektu

Aby zbudować cały projekt, wykonaj poniższą komendę z katalogu głównego:

```bash
mvn clean package
```

Komenda ta kompiluje kod, uruchamia testy i generuje artifact.

### 4.2 Uruchomienie Aplikacji

Po pomyślnym zbudowaniu uruchom aplikację za pomocą:

```bash
java -jar main/target/main-1.0-jar-with-dependencies.jar
```

### 4.3 Inicjalizacja Danych

Przy starcie aplikacja sprawdza, czy istnieje plik `hotel_saved.csv`:

- **Jeśli plik istnieje**: aplikacja pyta użytkownika, czy wczytać zapisane dane (`(yes/no)`)
- **Jeśli plik nie istnieje**: aplikacja wczytuje dane z pliku `hotel.csv`

Na podstawie wybranego pliku aplikacja inicjalizuje obiekty pokojów i gości z danymi ze struktury CSV.

## 5. Struktura Projektu

Projekt zorganizowany jest jako wielomodułowy projekt Maven z następującą strukturą:

```
Hotel/                                    (root, packaging = pom)
├─ pom.xml                               (parent POM — definiuje moduły)
├─ README.md                             (dokumentacja projektu)
├─ hotel.csv                             (plik startowy z danymi hotelowymi)
├─ hotel_saved.csv                       (plik zapisu stanu — tworzony dynamicznie)
│
├─ utils/                                (moduł pomocniczy)
│  ├─ pom.xml
│  └─ src/
│     ├─ main/java/pl/edu/agh/kis/pz1/hotel/utils/
│     │  ├─ MyMap.java                   (generyczna struktura danych)
│     │  └─ Map.java                     (interfejs dla MyMap)
│     └─ test/java/pl/edu/agh/kis/pz1/hotel/utils/
│        └─ MyMapTest.java               (testy jednostkowe MyMap)
│
└─ main/                                 (moduł aplikacji)
   ├─ pom.xml
   └─ src/
      ├─ main/
      │  ├─ java/pl/edu/agh/kis/pz1/
      │  │  ├─ app/
      │  │  │  └─ HotelManagerApp.java   (główna klasa aplikacji, REPL)
      │  │  ├─ command/
      │  │  │  └─ CommandProcessor.java  (przetwarzanie komend użytkownika)
      │  │  ├─ io/
      │  │  │  ├─ HotelDataLoader.java   (wczytywanie danych z CSV)
      │  │  │  └─ HotelDataSaver.java    (zapis danych do CSV)
      │  │  ├─ model/
      │  │  │  ├─ Hotel.java             (klasa reprezentująca hotel)
      │  │  │  ├─ Room.java              (klasa reprezentująca pokój)
      │  │  │  └─ Guest.java             (klasa reprezentująca gościa)
      │  │  └─ service/
      │  │     └─ HotelService.java      (logika biznesowa hotelowa)
      │  └─ resources/
      │     └─ logback.xml               (konfiguracja loggera SLF4J+Logback)
      └─ test/
         └─ java/pl/edu/agh/kis/pz1/
            ├─ io/
            │  └─ HotelDataLoaderTest.java
            |  |_ HotelDataSaverTest.java
            ├─ model/
            │  ├─ GuestTest.java
            │  ├─ RoomTest.java
            │  └─ HotelTest.java
            └─ service/
               └─ HotelServiceTest.java
```

## 6. Architektura i Komponenty

### 6.1 Model Danych (`model/`)

**Guest** — reprezentuje gościa przebywającego w hotelu
- Pola: `firstName`, `lastName`, `email` (opcjonalnie), `phoneNumber` (opcjonalnie), `notes` (opcjonalnie)
- Konstruktor podstawowy: `Guest(firstName, lastName)`
- Konstruktor pełny: `Guest(firstName, lastName, email, phoneNumber, notes)`

**Room** — reprezentuje pokój hotelowy
- Pola: `roomNumber`, `description`, `price`, `capacity`, `guests`, `checkinDate`, `checkoutDate`, `days`, `occupied`, `notes`
- Metody kluczowe:
    - `checkin(guests, checkinDate, days, notes)` — zameldowanie gości w pokoju
    - `checkout()` — wymeldowanie gości i obliczenie należności (na podstawie liczby nights)
    - `isAvailable()` — sprawdzenie, czy pokój jest wolny
    - `toString()` — zwrotna reprezentacja tekstowa pokoju

**Hotel** — reprezentuje hotel jako całość
- Przechowuje pokoje w strukturze `MyMap<Integer, Room>` (klucz: numer pokoju)
- Metody kluczowe:
    - `addRoom(room)` — dodanie pokoju do hotelu
    - `getRoom(roomNumber)` — pobranie pokoju po numerze
    - `listRooms()` — pobranie listy wszystkich pokoji
    - `listAvailableRooms()` — pobranie listy pokoji dostępnych

### 6.2 Logika Biznesowa (`service/`)

**HotelService** — zapewnia interfejs biznesowy do zarządzania hotelem
- Metody:
    - `checkin(roomNumber, guests, days)` — zameldowanie gościa (z datą bieżącą)
    - `checkin(roomNumber, guests, checkinDate, days, notes)` — zameldowanie z datą i notatkami
    - `checkout(roomNumber)` — wymeldowanie i wyliczenie opłaty
    - `view(roomNumber)` — pobranie informacji o pokoju
    - `listAllRooms()` — lista wszystkich pokojów
    - `listAvailableRooms()` — lista pokojów dostępnych
    - `listPrices()` — lista cen wszystkich pokojów

### 6.3 Interfejs Użytkownika (`app/`)

**HotelManagerApp** — główna klasa aplikacji
- Inicjalizuje loader danych, serwis hotelowy oraz procesor komend
- Uruchamia REPL (Read-Eval-Print Loop) obsługujący interaktywne polecenia
- Obsługuje wybór pliku startowego (saved vs. default)
- Wyświetla banner z tytułem aplikacji i dostępne komendy

### 6.4 Warstwę I/O (`io/`)

**HotelDataLoader** — wczytywanie danych z pliku CSV
- Statyczna metoda `loadFromCsv(filePath, hotelName)` wczytuje dane z CSV
- Parsuje pokoje, gościu, daty zameldowania i notatki
- Obsługuje błędy I/O i parsowania (loguje, zwraca pusty Hotel jeśli błąd)

**HotelDataSaver** — zapis danych do pliku CSV
- Statyczna metoda `saveToCsv(hotel, filePath)` zapisuje stan hotelu
- Formatuje gościu i daty do formatu CSV

### 6.5 Struktura Danych (`utils/`)

**MyMap** — generyczna implementacja mapowania klucz-wartość
- Realizuje interfejs `Map<K, V>`
- Wewnętrznie używa dwóch list (`keys`, `values`) do przechowywania par
- Metody:
    - `put(key, value)` — dodanie/aktualizacja pary
    - `get(key)` — pobranie wartości po kluczu
    - `remove(key)` — usunięcie pary
    - `contains(key)` — sprawdzenie obecności klucza
    - `keys()` — pobranie listy kluczy


## 6.6 Przetwarzanie Komend — CommandProcessor

Klasa `CommandProcessor` jest odpowiedzialna za parsowanie oraz wykonanie komend wpisywanych przez użytkownika w interaktywnym CLI.

- Komendy są dzielone na tokeny po białych znakach i rozpoznawane po pierwszym wyrazie (np. `checkin`, `checkout`).
- Obsługiwane komendy: `checkin`, `checkout`, `view`, `list`, `listAvailable`, `prices`, `save`.


### Checkin

```
- Składnia: `checkin <roomNumber> <firstName> <lastName> [...więcej gości] <days> [checkinDate] [notes]`
```

- Weryfikacja:
    - Numer pokoju musi być liczbą całkowitą.
    - Liczba dni jest obowiązkowa i musi być liczbą całkowitą.
    - Goście podawani są parami imię i nazwisko; ich liczba jest zmienna.
- `checkinDate` (opcjonalna) oczekuje formatu `yyyy-MM-dd`; jeśli pominięta, używa się daty bieżącej.
- `notes` to dowolny tekst po opcjonalnej dacie, np. preferencje gości.
- Komenda wykonuje check-in z podanymi parametrami i zwraca potwierdzenie lub komunikat o błędzie.


### Pozostałe komendy

- `checkout <roomNumber>` — wylogowanie i wyliczenie należności; zwraca kwotę lub informację o błędzie
- `view <roomNumber>` — wyświetla szczegóły pokoju lub błąd jeśli nie istnieje
- `list` — pokazuje listę wszystkich pokojów
- `listAvailable` — pokazuje tylko pokoje wolne
- `prices` — pokazuje ceny wszystkich pokojów
- `save` — zapisuje stan hotelu do pliku `hotel_saved.csv`


### Obsługa błędów

- Komendy i argumenty są walidowane z komunikatami:
    - "invalid room number"
    - "invalid number of days"
    - "missing number of days"
    - "incomplete guest name at position X"
    - "check-in failed"
    - "checkout failed"
- Nieznane polecenia zwracają "unknown command: <polecenie>".


### Przykład komendy `checkin`

```
checkin 101 Jan Kowalski 3 2025-10-10 preferuje ciszę
```

Zameldowuje gościa Jana Kowalskiego do pokoju 101 na 3 dni z datą 10 października 2025 i notatką.

***



## 7. Format Danych CSV

### 7.1 Struktura Pliku Wejściowego

Plik CSV zawiera nagłówek oraz wiersze danych w formacie:

```
roomNumber,description,price,capacity,guests,checkinDate,notes
```

**Kolumny:**

| Kolumna | Typ | Opis | Przykład |
|---------|-----|------|---------|
| `roomNumber` | Integer | Unikalny numer pokoju | `101` |
| `description` | String | Opis/nazwa pokoju | `jedynka` |
| `price` | Double | Cena pokoju za noc | `150.0` |
| `capacity` | Integer | Maksymalna liczba gości | `2` |
| `guests` | String | Goście (pipe-separated, opcjonalnie puste) | `Jan Kowalski\|Maria Nowak` |
| `checkinDate` | String | Data zameldowania (ISO yyyy-MM-dd, opcjonalnie puste) | `2025-11-15` |
| `notes` | String | Dodatkowe notatki (opcjonalnie puste) | `pokój klimatyzowany` |

### 7.2 Zasady Parsowania

**Parsowanie Gościa:**
- Pole `guests` zawiera gości oddzielonych znakiem `|` (pipe)
- Każdy gość reprezentowany jest jako `FirstName LastName`
- Loader bierze pierwsze dwa tokeny jako firstName i lastName; pozostałe części ignoruje
- Przykład: `Jan Kowalski|Maria Nowak` → dwóch gości

**Parsowanie Daty:**
- Jeśli pole `checkinDate` jest puste, loader używa bieżącą datę (`LocalDate.now()`)
- W przeciwnym razie parsuje datę w formacie ISO (`yyyy-MM-dd`)

**Notatki:**
- Jeśli pole `notes` (kolumna 7) nie istnieje w wierszu, zwracany jest pusty string
- Notatki mogą zawierać dowolny tekst

### 7.3 Przykładowy Plik Wejściowy

Przykładowe pliki CSV opisujące działanie aplikacji hotelowej:

***

Przykładowy plik wejściowy: hotel.csv

roomNumber,description,price,capacity,guests,checkinDate
101,jedynka,150.0,1,Kupidyna K.,2025-11-01
102,dwójka,200.0,2,,
103,dla podwójnych randek,300.0,4,Syzyf S.|Fidor F.,2025-11-03

- roomNumber: numer pokoju
- description: opis pokoju
- price: cena za noc
- capacity: maksymalna liczba gości
- guests: lista gości oddzielonych znakiem pipe (`|`), gość to `Imię Nazwisko`
- checkinDate: data zameldowania w formacie `yyyy-MM-dd`
- puste pole guests lub checkinDate oznacza brak zameldowanych gości

***

### 7.4 Przykładowy Plik Wyjściowy

Po zapisaniu stanu aplikacji (komenda `save`) plik `hotel_saved.csv` zawiera:

```csv
roomNumber,description,price,capacity,guests,checkinDate,notes
101,jedynka,150.0,1,Kupidyna K.,2025-11-01,preferuje ciszę
102,dwójka,200.0,2,,,
103,dla podwójnych randek,300.0,4,Syzyf S.|Fidor F.,2025-11-03,
```
- W pliku zapisowym dodatkowo pole `notes` z opcjonalnymi notatkami o pokoju
- Format gości i daty taki sam jak w pliku wejściowym
- Puste pola w `guests`, `checkinDate` i `notes` oznaczają brak danych w tych polach

## 8. Uruchamianie Testów

Projekt zawiera testy jednostkowe weryfikujące poprawność implementacji. Aby uruchomić testy, wykonaj:

```bash
mvn test
```

Testy obejmują:

- **HotelDataLoaderTest** — weryfikuje parsowanie danych z CSV, obsługę błędów i poprawne ładowanie pokojów i gościu
- **GuestTest, RoomTest, HotelTest** — testy modelu danych
- **HotelServiceTest** — testy logiki biznesowej
- **MyMapTest** — testy struktury danych `MyMap`

## 9. Przykład Użycia

Poniżej znajduje się przykładowa sesja z aplikacją:

```
================================================================
                      Hotel Toto
         goodmorning folks, this is hotel system Toto.
================================================================

commands:
  checkin <roomNumber> <firstName> <lastName> <days>
  checkout <roomNumber>
  view <roomNumber>
  list
  listAvailable
  prices
  save
  exit

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

## 10. Uwagi Implementacyjne

### 10.1 Obsługa Wejścia/Wyjścia

- Komunikaty dla użytkownika wypisywane są przez `System.out`
- Logi diagnostyczne są rejestrowane przez SLF4J z backendem Logback
- **Ważne:** Nie zamykaj `System.in` przy użyciu `Scanner(System.in)` — unikaj `try-with-resources` dla strumienia wejścia

### 10.2 Obliczanie Opłaty za Checkout

Opłata za wynajęcie pokoju jest obliczana na podstawie liczby pełnych nocy między datą zameldowania a datą wymeldowania:

```
total_cost = number_of_nights * price_per_night
```

Liczba nocy liczona jest jako różnica między datą bieżącą a datą zameldowania, niezależnie od zaplanowanego dnia wymeldowania.

### 10.3 Liczba Dni w CSV

W aktualnej implementacji `HotelDataLoader`, przy wczytywaniu danych z CSV, liczba dni zameldowania jest na stałe ustawiana na **3 dni**. Aby zmienić to zachowanie, zmodyfikuj wartość w metodzie `loadFromCsv()` w klasie `HotelDataLoader`.

### 10.4 Interfejs Map

Projekt zawiera własną implementację struktury danych `MyMap`, która implementuje interfejs `Map<K, V>`. Interfejs definiuje następujące operacje:

- `put(K, V)` — dodanie lub zaktualizowanie pary
- `get(K)` — pobranie wartości
- `remove(K)` — usunięcie pary
- `contains(K)` — sprawdzenie obecności
- `keys()` — pobranie listy kluczy

## 10. Obsługa Błędów

System w sytuacjach błędów przewiduje mechanizmy wykrywania i komunikowania problemów w kilku kluczowych obszarach:

### 10.1 Błędy przy wczytywaniu plików CSV

- Jeśli plik danych (`hotel.csv` lub `hotel_saved.csv`) nie istnieje lub nie można go odczytać, aplikacja loguje błąd i tworzy pusty obiekt hotelu bez pokoi.
- Błędy parsowania wierszy CSV (np. nieprawidłowy format liczbowy czy data) są łapane przez mechanizm obsługi wyjątków i logowane, dzięki czemu aplikacja nie przerywa działania, a złe dane są ignorowane.


### 10.2 Błędne dane w CSV

- Puste pola tam, gdzie są wymagane dane (np. liczba dni pobytu w wierszu CSV) skutkują pominięciem check-in dla danego pokoju.
- W polu gości, jeśli format jest niezgodny (np. brak imienia lub nazwiska), dany token jest pomijany, a w przypadku niemożności poprawnego sparsowania wzorem `FirstName LastName` cała pozycja jest ignorowana.


### 10.3 Błędy w komendach użytkownika

- Komunikaty dla użytkownika w przypadku błędów wprowadzonych komend są czytelne i informują, co zostało źle podane, np.:
    - „invalid room number” — gdy numer pokoju to nie liczba całkowita lub pokój nie istnieje
    - „invalid number of days” — gdy liczba dni nie jest liczbą całkowitą
    - „missing number of days” — gdy brakuje argumentu dni pobytu
    - „incomplete guest name at position X” — gdy para imienia i nazwiska gościa jest niekompletna
    - „check-in failed” — gdy zameldowanie nie udało się (np. pokój zajęty)
    - „checkout failed” — gdy wymeldowanie jest niemożliwe (np. pokój wolny)
- W przypadku nieznanej komendy system zwraca komunikat „unknown command: <nazwa>”.


### 10.4 Bezpieczne działanie i logowanie

- Wszystkie wyjątki krytyczne są logowane za pomocą SLF4J z backendem Logback, co umożliwia diagnostykę i naprawę problemów.
- Aplikacja dąży do bezpiecznego działania bez przerywania pracy w przypadku błędów w danych i komendach.

***

## 11. Rozszerzenia i Ulepszeń

Poniżej wymieniono potencjalne kierunki rozwoju projektu:

- Obsługa bardziej złożonych formatów CSV (nazwiska z wieloma częściami, pola z cudzysłowami, przecinki w wartościach) — rozważ użycie biblioteki Apache Commons CSV
- Baza danych zamiast CSV
- Interfejs graficzny (GUI) zamiast CLI
- System autoryzacji i uprawnień dla personelu hotelowego
- Rozliczanie podatków VAT w kalkulacji opłat
- Eksport raportów w formacie PDF
