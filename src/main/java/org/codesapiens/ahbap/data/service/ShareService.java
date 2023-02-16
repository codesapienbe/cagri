package org.codesapiens.ahbap.data.service;

import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.codesapiens.ahbap.data.entity.TagEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {

    public String shareOnTwitter(PersonEntity savedPerson, List<ItemEntity> requiredItems,
                                List<TagEntity> annotations, List<TagEntity> hashtags) {
        StringBuilder sb = new StringBuilder("https://twitter.com/share")
                .append("?")
                .append("text=ACİL YARDIM ÇAĞRISI!!! ")
                .append(("\n"))
                .append("İhtiyacım olanlar: ")
                .append(("\n"))
                .append(
                        requiredItems.stream()
                                .map(ItemEntity::getTitle)
                                .collect(Collectors.joining("+"))
                )
                .append("&")
                .append("url=")
                .append("https://maps.google.com/maps?z=12")
                .append("&")
                .append("t=m")
                .append("&")
                .append("q=loc:")
                .append(savedPerson.getLatitude())
                .append("+")
                .append(savedPerson.getLongitude());

        sb.append("&via=");

        for (int i = 0; i < annotations.size(); i++) {
            TagEntity ann = annotations.get(i);
            sb.append(ann.getTitle());
            if (i < annotations.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("&hashtags=");
        for (int i = 0; i < hashtags.size(); i++) {
            TagEntity htag = hashtags.get(i);
            sb.append(htag.getTitle());
            if (i != hashtags.size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    public String shareOnWhatsApp(PersonEntity savedPerson, List<ItemEntity> requiredItems) {

        Double[] from = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        Double[] to = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        final var directionsUrl = "https://www.google.com/maps/dir" + "/" + from[0] + "," + from[1] + "/" + to[0] + ","
                + to[1] + "/@" + to[0] + "," + to[1];


        String absoluteUrl = "https://wa.me?text=" +
                "ACİL%20YARDIM%20ÇAĞRISI!!!" +
                "%20" +
                "İhtiyacım%20olanlar:" +
                "%20" +
                requiredItems.stream()
                        .map(it -> it.getTitle().replace(" ", "%20"))
                        .collect(Collectors.joining("+")) +
                "%20" +
                "Konumum:" +
                "%20" +
                directionsUrl;

        return absoluteUrl;
    }


}
