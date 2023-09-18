package fr.nil.backedflow.controllers;


import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
public class AssetController {

    @GetMapping(
            value = "/logo.png",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public @ResponseBody byte[] getLogoImage() throws IOException {
        ClassPathResource imgFile = new ClassPathResource("logo_dark.png");
        return IOUtils.toByteArray(imgFile.getInputStream());
    }
}
