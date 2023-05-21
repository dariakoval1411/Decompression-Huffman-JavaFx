# **HUFMMAN DECODING**

Author Daria Koval

**Aplikacja do dekompresji danych na podstawie algorytmu Huffman.**

# **Opis**

Algorytm Huffmana to skuteczny sposób kompresji i dekompresji plików.

Algorytm odczytuje częste znaki z pliku wejściowego i zastępuje je krótszym kodem binarnym. Oryginalny plik można utworzyć ponownie bez utraty jakiegokolwiek bitu.


Algorytm składa się z takich etapów:

1.**Pierwszy etap** polega na przeczytaniu pliku zakodowanego przez Huffmana.

2.**Drugi etap** polega na budowaniu drzewa Huffmana. Informacje o częstotliwości używane podczas kompresji są wymagane do rekonstrukcji. Informacje te mogą być przechowywane wraz ze skompresowanymi danymi lub pozyskiwane z oddzielnego źródła. Drzewo Huffmana powinno być skonstruowane w taki sam sposób, jak podczas kompresji.

3.**Następnym etapem** jest dekodowanie skompresowanych plików. Zaczynamy od korzenia drzewa Huffmana i przechodzimy przez nie na podstawie bitów w skompresowanych danych. Przechodząc przez drzewo, za każdym razem, gdy dochodzimy do węzła liścia, wyprowadzamy odpowiedni symbol i kontynuujemy dekodowanie pozostałych bitów.

4.**Ostatnim etapem** zapisujemy zdekompresowane dane do wyjścowego pliku.


# **Opis kodu Algorytma**

## **HuffmanNode.java**
``` java

public record HuffmanNode(
        Integer data,
        String code,
        HuffmanNode leftChild,
        HuffmanNode rightChild
) implements Comparable<HuffmanNode> {

    public boolean isLeaf() {
        return (leftChild() == null) && (rightChild() == null);
    }

    public HuffmanNode traverse(char direction) {
        if (direction == '0')
            return leftChild;
        else
            return rightChild;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return code.compareTo(o.code());
    }

    public HuffmanNode getLeftChild(){
        return leftChild;
    }

    public HuffmanNode getRightChild(){
        return rightChild;
    }

    public int getData(){
        return data;
    }

    public String getCode() {
        return code;
    }
}

```

**Klasa HuffmanNode() ma następujące właściwości:**

**dane**: Wartość danych przechowywana w węźle.

**code**: Kod Huffmana powiązany z węzłem.

**leftChild**: lewy węzeł podrzędny bieżącego węzła.

**rightChild**: prawy węzeł podrzędny bieżącego węzła.

Klasa implementuje również interfejs Comparable, umożliwiając porównywanie wystąpień węzłów na podstawie ich wartości kodu.

Także **Klasa** udostępnia metody sprawdzania, czy węzeł jest węzłem liścia **(isLeaf())**, przechodzenia przez drzewo na podstawie zadanego kierunku **(traverse())** oraz uzyskiwania dostępu do lewego i prawego węzła potomnego **(getLeftChild()** i **getRightChild())**.

## **Tree.java**
``` java
public class Tree {
    private final HuffmanNode root;

    public Tree(Map<Integer, String> codes) {
        List<HuffmanNode> nodeList = codes.entrySet().stream()
                .map(n -> new HuffmanNode(n.getKey(), n.getValue(), null, null))
                .sorted()
                .collect(Collectors.toList());

        int level = codes.values().stream().mapToInt(String::length).max().orElseThrow(NoSuchElementException::new);

        while (level >= 0) {
            int currentLevel = level;

            List<HuffmanNode> nodesAtLevel = nodeList.stream().filter(n -> n.code().length() == currentLevel).collect(Collectors.toList());

            for (int i = 0; i < nodesAtLevel.size() / 2; i++) {
                HuffmanNode join = new HuffmanNode(
                        -2,
                        nodesAtLevel.get(2 * i).code().substring(0, level - 1),
                        nodesAtLevel.get(2 * i),
                        (nodesAtLevel.size() % 2 == 1) ? null : nodesAtLevel.get(2 * i + 1)
                );

                nodeList.add(join);
            }

            Collections.sort(nodeList);

            level--;
        }

        root = nodeList.get(0);
    }

    public HuffmanNode getRoot() {
        return root;
    }

}
```

Klasa **Tree()** pobiera jako dane wejściowe obiekt **Map<Integer, String>** o nazwie **codes**.

Klucze mapy reprezentują wartości całkowite, a wartości reprezentują kody Huffmana powiązane z tymi wartościami.

Wewnątrz konstruktora kod tworzy listę obiektów **HuffmanNode** na podstawie mapy kodów.

Lista obiektów **HuffmanNode** jest sortowana na podstawie ich naturalnej kolejności, która jest zdefiniowana przez implementację Comparable w klasie **HuffmanNode**.

Kod określa maksymalną długość kodu wśród kodów Huffmana na mapie kodów. Kod wchodzi w pętlę, która iteruje od maksymalnej długości kodu do zera. Ta pętla służy do konstruowania drzewa **Huffmana** poziom po poziomie.

Wewnątrz pętli, dla każdego poziomu, kod filtruje **nodeList**, aby uzyskać węzły na bieżącym poziomie.

Następnie kod przechodzi przez węzły na bieżącym poziomie, tworząc nowe obiekty **HuffmanNode** w celu łączenia par węzłów. Jeśli na bieżącym poziomie jest nieparzysta liczba węzłów, ostatni węzeł nie jest połączony z żadnym innym węzłem.

Połączone obiekty **HuffmanNode** są dodawane do **nodeList**. Po połączeniu węzłów lista węzłów jest ponownie sortowana, aby zachować poprawną kolejność.

Poziom jest zmniejszany, a pętla jest kontynuowana, aż wszystkie poziomy zostaną przetworzone.
W końcu korzeń drzewa **Huffmana** jest ustawiony jako pierwszy element w nodeList.

Metoda **getRoot()** służy do pobierania korzenia drzewa Huffmana.

## **HuffmanDecoder**
``` java
public class HuffmanDecoder {

    private static Map<Integer, String> codes;

    public static void decode(FileInputStream input, FileOutputStream output) throws IOException {
        codes = new HashMap<>();

        BitReader inputReader = new BitReader(input);

        StringBuilder buffer = new StringBuilder();

        int codeLength;
        String code;

        // load codes
        for (int i = 0; i < 256; i++) {
            // read code length
            for (int j = 0; j < 8; j++)
                buffer.append(inputReader.readBit());

            codeLength = Integer.parseInt(buffer.toString(), 2);
            buffer.setLength(0);

            // read code
            for (int j = 0; j < codeLength; j++)
                buffer.append(inputReader.readBit());

            code = buffer.toString();
            buffer.setLength(0);

            // store code
            if (codeLength != 0)
                codes.put(i, code);
        }

        Tree tree = new Tree(codes);
        HuffmanNode pointer = tree.getRoot();

        char charBuffer;

        while ((charBuffer = inputReader.readBit()) != '2') {
            pointer = pointer.traverse(charBuffer);

            if (pointer.isLeaf()) {
                output.write(pointer.data().byteValue());

                pointer = tree.getRoot();
            }
        }
    }
    public static Map<Integer, String> getCodes() {
        return codes;
    }
}
```
Kod ten odczytuje dane zakodowane w systemie Huffmana z pliku, rekonstruuje drzewo Huffmana przy użyciu załadowanych kodów i dekoduje skompresowane dane z powrotem do ich pierwotnej postaci. Zdekodowane dane są zapisywane do pliku wyjściowego.

## **BitReader**


Klasa **BitReader** umożliwia odczytywanie poszczególnych bitów z **FileInputStream** poprzez śledzenie bieżącego bajtu, bieżącej pozycji bitu w bajcie i obsługę warunków końca strumienia. Zapewnia metodę **readBit** do pobrania następnego bitu ze strumienia wejściowego.

Metoda **readBit** służy do odczytu pojedynczego bitu ze strumienia wejściowego. Zwraca znak **„0”, „1” lub „2”** wskazujący wartość bitu lub koniec strumienia wejściowego.
Jeśli bitPosition wynosi **7**, oznacza to, że bieżący bajt został w pełni przetworzony, więc kod próbuje odczytać następny bajt ze strumienia wejściowego. Jeśli **inputStream.read()** zwróci wartość **-1**, oznacza to, że nie ma więcej bajtów do odczytania i zwracane jest **„2”**, wskazujące koniec danych wejściowych.


# **Interfejs Progarmu**

![Image alt](https://github.com/dariakoval1411/Decompression-Huffman-JavaFx/blob/main/1.png)

**Display Output Image** - pokazuje zdekmpresowany plik

**Decode** - dekompresuje plik wejściowy

**Show Tree** - wyświetlia drzwewo Huffmana

**Input path** - ścieżka plika wejściowego

**Output path** - ścieżka plika wyjściowego

# **Stosowanie**

![Image alt](https://github.com/dariakoval1411/Decompression-Huffman-JavaFx/blob/main/2.png)

![Image alt](https://github.com/dariakoval1411/Decompression-Huffman-JavaFx/blob/main/3.png)


![Image alt](https://github.com/dariakoval1411/Decompression-Huffman-JavaFx/blob/main/4.png)
