

import java.util.Locale;

public class HashTable <K,V>{
	
	
	private int size=997;
	public int collisionCount=0;
	private int prime = 1009; // table size ilk olarak 997 olarak belirlendiginiden, 
							// kendi compression fonksiyonumda (MAD) kullanacagim asal sayiyi da ilk basta kendim belirliyorum. resize() yapinca guncelleyecegim.
	
	
	HashEntry<K,V> table[];
	HashEntry<K,V> tempTable[];
	
	
	@SuppressWarnings("unchecked")
	public HashTable() { // to create
		//this.size = size;
		table = new HashEntry[size];
		for (int i = 0; i < size; i++) {
			table[i]=null;
		}
	}
	
	
	public int myHashCodeFunction(K key) { // h1(x) --> implement by me:)
		String keyy = key.toString().toLowerCase(Locale.ENGLISH);
		int z =37; // prime number
		int hashCode=0;
		int w = keyy.length(); // daha fazla collison olmamasi icin sonradan bunu ekledim.
		// collison sayisi yari yariya azaldi fakat yine de PAF kadar etkili degil :(
		for (int i = 0; i < keyy.length(); i++) {
			if(i%2==0) {
				
				hashCode += z* (int)  Math.abs((int) keyy.charAt(i) - 96) *w ; 
			}
			else if(i%2==1){
				hashCode -= z* (int)  Math.abs((int) keyy.charAt(i) - 96) *w;
			}
		}
		hashCode = Math.abs(hashCode); // negatif cikmamasi icin
		return hashCode;
	}

	public int hashCodeFunction(K key) { // h1(x) --> Polynomial Accumulation Function (PAF)
		String keyy= key.toString().toLowerCase(Locale.ENGLISH); // to lowercase
		
		int z = 37; // prime number i used. 33, 39.. also can be used.
		int poly[] = new int[keyy.length()]; // for horner 
		int n = keyy.length();
		for (int i = 0; i <n; i++) { 	
			poly[i] = (int)  Math.abs((int) keyy.charAt(i) - 96); 
		}
		
		// i use horner's rule to handle overflows
		int polyNumber = poly[0];
		for (int i = 1; i < n; i++) {
			polyNumber = polyNumber*z + poly[i];
		}
		polyNumber = Math.abs(polyNumber); // math.abs() is used to prevent negative values
		return polyNumber;
	}
	
	private int findPrimeforMAD(int size) {
		// table size degistiginde, MAD fonksiyonunda kullandigim asal sayinin da yeni size degerine gore guncellenmesi gerekiyor
		// cunku asal sayi > table size olmali...
		prime = size+1; // bu sekilde asal sayimin table size'dan buyuk olacagini garantiliyorum.
		while(true) {
			int c=0;
			for (int i = 1; i <= prime; i++) {
				int k = prime%i;
					if(k==0) 
						c++;
				}
				if(c!=2) 
					prime++;
				else if(c==2)
					break;
			}
		return this.prime= prime; // guncellendi
	}
	
	public int findHashIndex(K key) { // h2(h1(x)) --> compression func
		// h2 (y) = ((ay + b) mod P) mod N
		// P is a prime number larger than N
		// a and b are nonnegative integers chosen randomly from the interval [0, P-1]	
		
		//kendi yazdigim compression function, MAD (Multiply, Add and Divide) kullandim. 
													//denemek icin bunlari acip, "//paf" olani yorum satiri haline getirin
													// uzun surerse biraz bekleyin
													// get kisminda da yorum satiri yapmaniz gereken bir yer var....
										
//		int a = 13; // prime number i used for a
//		int b=11; // prime number i used for b
//		int y = myHashCodeFunction(key);
//		int index = Math.abs(((a*y + b) % prime)) % size;
		
		
		int index= hashCodeFunction(key)%size;  // THIS IS FOR PAF ( Division compress func ) --> digeri icin bu satiri yorum satirina alin
		return index;
	}
	
	@SuppressWarnings("unchecked")
	public void put(K key, V value) {
		int currentCapacity = size();
		int tableLength = table.length;
		int loadFactor = (int)(currentCapacity*10/tableLength);
		
		// kelimeyi yerlestirmeden once resize yapmam gerekiyor mu diye kontrol ediyorum.... sonrasinda kelimeyi yerlestiriyorum.
		if(loadFactor>=5) { // 0.7'ye gore de resize yazilabilir, degistirmek icin 5 yerine 7 yazin
			resize();
			//System.out.println("Resized...");
		}
		
		int hashIndex = findHashIndex(key); // h2(h1(x)) index bulunur
		
		HashEntry<K,V> newHash = new HashEntry<K,V>(key,value);
		int DIB=0;
		while(true) {
			if(table[hashIndex]==null) { // if null, (key,word) can be inserted without any problem
				table[hashIndex] = new HashEntry<K,V>(newHash.getKey(),newHash.getValue());
				break; // break the loop, because insertion is succeed
			}
			else if(table[hashIndex]!=null && table[hashIndex].getKey().toString().equals(newHash.getKey())) { // if same word is added again, value(count) increases 
				table[hashIndex].setValue((V) String.valueOf((Integer.parseInt(table[hashIndex].getValue().toString()) + 1))); 
				break; // break the loop
			}
			collisionCount++; // yerlestirme isleminin olmadigi her durum collison oluyorsa, bu buraya yazilmali diye dusunuyorum..
			int DIB2 = hashIndex - findHashIndex(table[hashIndex].getKey()); // bos olmayan indexteki datanin dib degeri
			if(DIB>DIB2) {
				// yerlestirecegim yerdeki datayi kaybetmemek icin ilk olarak tempe atiyorum
				HashEntry<K,V> temp= new HashEntry<K,V>(table[hashIndex].getKey(),table[hashIndex].getValue()); 
				table[hashIndex]=null; // this is not necessary
				table[hashIndex] = newHash; // inserted
				newHash = new HashEntry<K,V>(temp.getKey(),temp.getValue()); // artik yerlestirmem gereken data, biraz once tempe attigim yani bulundugu yerden cikardigim entry
																						// sirada onu yerlestirmek var
				// yani aslinda bu while dongusu bos index bulunana kadar devam ediyor diyebilirim, cunku bos index bulunup yerlestirildiginde tempe atilacak bir sey olmayacak. ilk if kosuluna girip dongu sonlanacak
				DIB = hashIndex - findHashIndex(newHash.getKey()); // dib hesaplama
			}
			DIB++; // girmesi gereken indexten 1 uzaklasacagindan dib degerini de artiriyorum
			hashIndex++; // bir sonraki hashe bakiyorum
			hashIndex = hashIndex % table.length; // out of bounds vermemesi icin, mod aliyorum. eger hashIndex=table.length olmussa, bu islem sonucu hashIndex=0 oluyor
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void resize() { 
		int newSize =  table.length*2;
		while(true) {
			int count=0;
			for (int i = 1; i <= newSize; i++) { // Checking whether newsize is prime or not
				int kalan = newSize%i;
				if(kalan==0) count++; //  zero means divided
			}
			if(count!=2) // it's not a prime number, I have to increase the number and check again
				newSize++;
			else if(count==2) //  prime number has 2 divisors. itself and 1. if found,
				break; // break the loop
		}
		
		// yeni size bulundu, sirada yeni hashtable olusturup eskisindekileri atmak var.
		
		this.size=newSize;     // size guncellendi, hash func ve hashtable olusturmak icin de guncellemis oldum
		findPrimeforMAD(size); // size guncellenince, prime number da guncellendi.... ( kendi hash(compression) fonksiyonum icin)
		tempTable = new HashEntry[size];
		for (int i = 0; i < table.length; i++) {
			if(table[i]!=null) {
				tempTable[i]= table[i]; // onceden yerlesenleri tempe attim
			}
		}
		
		table = new HashEntry[size]; // mevcut table'in size'i guncellendi
		// simdi temptekileri, guncellenen size'li hash functiona sokup, table'a geri aticam
		for (int i = 0; i < tempTable.length; i++) {
			if(tempTable[i]!=null) {
				int newIndex = findHashIndex(tempTable[i].getKey());
				HashEntry<K,V> newHash = new HashEntry<K,V>(tempTable[i].getKey(),tempTable[i].getValue());
				int DIB=0;
				while(true) {
					if(table[newIndex]==null) {
						table[newIndex] = new HashEntry<K,V>(newHash.getKey(),newHash.getValue());
						break;
					}
					collisionCount++; // resize'da da collison gerceklesiyor...
					int DIB2= newIndex - findHashIndex(table[newIndex].getKey()); // mecvut olarak bulunanin dib hesapladim
					if(DIB>DIB2) { // dib yerlesir, dib2 olan tempe atilir
						HashEntry<K,V> temp = new HashEntry<K,V>(table[newIndex].getKey(),table[newIndex].getValue());
						table[newIndex]=null;
						table[newIndex] = newHash; // yerlesti
						newHash = temp; // artik yeni yerlestirecegim yukarda temp olarak tanimladigim entry
						DIB = newIndex - findHashIndex(newHash.getKey());
					}
					DIB++;
					newIndex++; // bir sonraki indexe bakmam lazim
					newIndex = newIndex%table.length; // outofbounds onlenir
					
				}
			}
		}
	}

	
	public void getInfo(K key) { //  key, count, and index of the word should be printed.
		boolean search = true;
		int hash = findHashIndex(key); //index 
		int hashCode = hashCodeFunction(key);   // paf sonucu 
		//int hashCode = myHashCodeFunction(key);    ----- yhf icin bu satiri acin, yukardakini yorum satiri haline getirin.
		//long startTime =  System.nanoTime();
		while(search) { 
			if(table[hash]!=null && table[hash].getKey().toString().equals(key.toString().toLowerCase(Locale.ENGLISH))) {
				//long endTime =  System.nanoTime();
				//long time = endTime - startTime;
				System.out.println("Key--> " + hashCode +
						"\nCount--> " + table[hash].getValue() + 
						"\nIndex--> " + hash  ); // "\nTime spent--> " + time
				search=false; // bulursa, break the loop
			}
			if(table[hash]==null) { // hashtable'i dolasmis ama kelimeyi bulamamis demek
				System.out.println(key + " --> Not exist in hashtable!!");
				search=false; // stop/break the loop
			}	
			hash = (hash+1) % table.length; // bir sonraki indexe bakmali, cunku coll sonucu probing yapilmis olabilir
		}
	}
	
	public int size() { // null olmayan key countu, resize icin gerekli
		int size=0;
		for (int i = 0; i < table.length; i++) {
			if(table[i]!=null)
				size++;
		}
		return size;
	}
	
	public int lenghtofHashTable() { // lenght of hash table, main'de gosterebilmek icin yazdim
		int size=0;
		for (int i = 0; i < table.length; i++) {
			size++;
		}
		return size;
	}
	
	public int totalWordCount() { // total value/count
		int wordCount=0;
		for (int i = 0; i < table.length; i++) {
			if(table[i]!=null) 
				wordCount+= Integer.parseInt(table[i].getValue().toString());
		}
		return wordCount;
	}
	
}
