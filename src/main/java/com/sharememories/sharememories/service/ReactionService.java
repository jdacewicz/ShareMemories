package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.ReactionRepository;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {

    private ReactionRepository reactionRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public Optional<Reaction> getReaction(Integer id) {
        return reactionRepository.findById(id);
    }

    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    public Reaction createReaction(String name, MultipartFile file) {
        Reaction reactionJson = new Reaction();
        reactionJson.setName(name);
        try {
            String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
            FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, fileName, file);
            reactionJson.setImage(fileName);
        } catch (IOException e) {
            System.out.println(e);
        }
        return reactionRepository.save(reactionJson);
    }

    public Reaction replaceReaction(Reaction reaction) {
        return reactionRepository.findById(reaction.getId())
                .map(r -> {
                    r.setName(reaction.getName());
                    return reactionRepository.save(r);
                }).orElseGet(() -> reactionRepository.save(reaction));
    }

    public Optional<Reaction> replaceReaction(Reaction reaction, MultipartFile file) {
        return reactionRepository.findById(reaction.getId())
                .map(r -> {
                    try {
                        r.setName(reaction.getName());
                        String imageName = (r.getImage() == null) ? FileUtils.generateUniqueName(file.getOriginalFilename()) : r.getImage();

                        FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, imageName, file);
                        r.setImage(imageName);
                        return reactionRepository.save(r);
                    } catch (IOException e) {
                        return null;
                    }
                });
    }

    public boolean deleteReaction(Integer id) {
        Optional<Reaction> reaction = reactionRepository.findById(id);
        if (reaction.isPresent()) {
            try {
                if (reaction.get().getImage() != null)
                    FileUtils.deleteFile(Reaction.IMAGES_DIRECTORY_PATH, reaction.get().getImage());
                reactionRepository.delete(reaction.get());
                return true;
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return false;
    }
}
