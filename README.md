# konum çağrı

Bu proje, Spring Boot ile kendi Vaadin uygulamanızı oluşturmak için bir başlangıç noktası olarak kullanılabilir.
Başlamanız için gerekli tüm yapılandırmayı ve bazı yer tutucu dosyaları içerir.

## Uygulamayı çalıştırma

Proje standart bir Maven projesidir. Komut satırından çalıştırmak için,
mvnw` (Windows) veya `./mvnw` (Mac & Linux) yazın, ardından açın
Tarayıcınızda http://localhost:8080.

Ayrıca projeyi herhangi bir IDE'de yaptığınız gibi tercih ettiğiniz IDE'ye aktarabilirsiniz.
Maven projesi. Daha fazla bilgi için [Vaadin projeleri farklı projelere nasıl aktarılır
IDE'ler](https://vaadin.com/docs/latest/flow/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans ve VS
Code).

## Üretime Dağıtma

Bir üretim derlemesi oluşturmak için `mvnw clean package -Pproduction` (Windows) çağrısını yapın,
veya `./mvnw clean package -Pproduction` (Mac ve Linux).
Bu, tüm bağımlılıkları ve ön uç kaynaklarını içeren bir JAR dosyası oluşturacaktır,
dağıtılmaya hazırdır. Dosya, derleme tamamlandıktan sonra `target` klasöründe bulunabilir.

JAR dosyası oluşturulduktan sonra aşağıdakileri kullanarak çalıştırabilirsiniz
`java -jar target/meeloper-1.0-SNAPSHOT.jar`

## Proje yapısı

- src/main/java` içindeki `MainLayout.java` navigasyon kurulumunu içerir (yani
  yan/üst çubuk ve ana menü). Bu kurulum şunları kullanır
  [Uygulama Düzeni](https://vaadin.com/components/vaadin-app-layout).
- src/main/java` içindeki `views` paketi uygulamanızın sunucu tarafı Java görünümlerini içerir.
- `frontend/` içindeki `views` klasörü uygulamanızın istemci tarafı JavaScript görünümlerini içerir.
- frontend/` içindeki `themes` klasörü özel CSS stillerini içerir.

## Yararlı bağlantılar

- vaadin.com/docs](https://vaadin.com/docs) adresindeki belgeleri okuyun.
- vaadin.com/tutorials](https://vaadin.com/tutorials) adresindeki eğitimleri takip edin.
- Eğitim videolarını izleyin ve [vaadin.com/learn/training](https://vaadin.com/learn/training) adresinden sertifika
  alın.
- start.vaadin.com](https://start.vaadin.com/) adresinde yeni projeler oluşturun.
- UI bileşenlerini ve kullanım örneklerini [vaadin.com/components](https://vaadin.com/components) adresinde arayın.
- Vaadin yeteneklerini gösteren kullanım örneği uygulamalarını görüntüleyin
  vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos) adresinde bulabilirsiniz.
- Vaadin'in özel CSS olmadan herhangi bir kullanıcı arayüzü oluşturmayı sağlayan CSS yardımcı sınıfları setini keşfedin
  docs](https://vaadin.com/docs/latest/ds/foundation/utility-classes).
- Vaadin Cookbook](https://cookbook.vaadin.com/)'da yaygın kullanım durumlarına yönelik çözümlerin bir koleksiyonunu
  bulun.
- Eklentileri [vaadin.com/directory](https://vaadin.com/directory) adresinde bulabilirsiniz.
- Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) üzerinden sorularınızı sorun veya
  Discord kanalımız](https://discord.gg/MYFq5RTbBn).
- Sorunları bildirin, [GitHub](https://github.com/vaadin/platform)'da çekme istekleri oluşturun.

## Docker kullanarak dağıtma

Projenin Docker'laştırılmış sürümünü derlemek için

```
docker build . -t meeloper:latest
```

Docker görüntüsü doğru bir şekilde oluşturulduktan sonra, yerel olarak

```
docker run -p 8080:8080 meeloper:latest
```

## Kubernetes kullanarak dağıtma

Burada Docker Desktop'taki Kubernetes kümesinin çalıştığını varsayıyoruz (ayarlardan etkinleştirilebilir).

Önce uygulamanız için Docker görüntüsünü oluşturun. Daha sonra Docker görüntüsünü kümeniz için kullanılabilir hale
getirmeniz gerekir. İle
Docker Masaüstü Kubernetes, bu otomatik olarak gerçekleşir. Minikube ile `eval $(minikube docker-env)` komutunu
çalıştırabilir ve ardından
kullanılabilir hale getirmek için görüntüyü oluşturun. Diğer kümeler için, bir Docker deposunda yayınlamanız veya
kümesi için belgeler.

Birlikte verilen `kubernetes.yaml`, 2 pod (sunucu örneği) ve bir yük dengeleyici hizmeti içeren bir dağıtım kurar.
Yapabilirsiniz
kullanarak uygulamayı bir Kubernetes kümesine dağıtın

```
kubectl apply -f kubernetes.yaml
```

Her şey çalışıyorsa, http://localhost:8000/ adresini açarak uygulamanıza erişebilirsiniz.
Eğer 8000 portunda çalışan başka bir şeyiniz varsa, `kubernetes.yaml` dosyasında yük dengeleyici portunu değiştirmeniz
gerekir.

İpucu: Eğer isteklerinizin hangi pod'a gittiğini anlamak istiyorsanız, şu değeri ekleyebilirsiniz
'in `VaadinServletRequest.getCurrent().getLocalAddr()` kısmını kullanıcı arayüzünüzde bir yere ekleyin.

### Sorun Giderme

Bir şey çalışmıyorsa, neyin konuşlandırıldığını ve durumlarını görmek için aşağıdaki komutlardan birini
deneyebilirsiniz.

```
kubectl get pods
kubectl get hizmetleri
kubectl get dağıtımları
```

Podlar `Container image "meeloper:latest" is not present with pull policy of Never` diyorsa, o zaman oluşturmamışsınız
demektir
uygulamanız Docker kullanıyor veya adda bir uyumsuzluk var. Hangi görüntülerin olduğunu görmek için `docker images ls`
kullanın
mevcut.

Daha fazla bilgiye ihtiyacınız varsa

```
kubectl cluster-info dump
```

Bu muhtemelen size çok fazla bilgi verecektir ancak bir sorunun nedenini ortaya çıkarabilir.

Tüm dağıtımınızı kaldırmak ve baştan başlamak istiyorsanız

```
kubectl delete -f kubernetes.yaml
```

