package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.PostRepository;
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
    private PostRepository postRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository, PostRepository postRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
    }

    public Optional<Reaction> getReaction(Integer id) {
        return reactionRepository.findById(id);
    }

    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    public void deleteReaction(Integer id) {
        Optional<Reaction> foundReaction = reactionRepository.findById(id);

        if (foundReaction.isPresent()) {
            Reaction reaction = foundReaction.get();
            try {
                FileUtils.deleteFile(Reaction.IMAGES_DIRECTORY_PATH, reaction.getImage());
                reactionRepository.delete(reaction);
                reactionRepository.flush();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public Reaction replaceReaction(String name, MultipartFile file, Integer id) {
        return reactionRepository.findById(id).map(reaction -> {
            reaction.setName(name);
            replaceFile(reaction.getImage(), file);

            return reactionRepository.save(reaction);
        }).orElseGet(() -> {
            Reaction newReaction = new Reaction();
            newReaction.setName(name);
            newReaction.setImage(uploadImage(file));

            return reactionRepository.save(newReaction);
        });
    }

    public Reaction createReaction(String name, MultipartFile file) {
        Reaction reactionJson = new Reaction();
        reactionJson.setName(name);
        reactionJson.setImage(uploadImage(file));

        return reactionRepository.save(reactionJson);
    }

    public void reactToPost(Integer reactionId, Long postId) {
        reactionRepository.findById(reactionId).map(reaction -> {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                reaction.addPost(post.get());
            }
            return reactionRepository.save(reaction);
        });
    }

    public String uploadImage(MultipartFile file) {
        String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
        try {
            FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, fileName, file);
            return fileName;
        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }

    public void replaceFile(String fileName, MultipartFile file) {
        try {
            FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, fileName,file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
