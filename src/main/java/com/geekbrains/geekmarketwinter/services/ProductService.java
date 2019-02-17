package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.CategoryRepository;
import com.geekbrains.geekmarketwinter.repositories.ProductRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private ImageSaverService imageSaverService;
    private CategoryRepository categoryRepository;

    @Autowired
    public void setImageSaverService(ImageSaverService imageSaverService) {
        this.imageSaverService = imageSaverService;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void importFromFile(MultipartFile file){
        try {
            InputStreamReader r = new InputStreamReader(file.getInputStream());
            CSVReader reader = new CSVReader(r, ';', '"', 1);
            String[] nextLine; //Категория;Код;Цена;Название;Краткое описание;Полное описание
            ArrayList<Product> products = new ArrayList<>();
            while ((nextLine=reader.readNext())!=null){
                Optional<Product> productOptional = productRepository.findOneByVendorCode(nextLine[1]);
                Product product;
                if (productOptional.isPresent()){
                    product=productOptional.get();
                }
                else {
                    product = new Product();
                }
                if(product.getCategory()==null || !product.getCategory().getTitle().equals(nextLine[0])){
                    Optional<Category> categoryOptional = categoryRepository.findByTitle(nextLine[0]);
                    if(categoryOptional.isPresent()){
                        product.setCategory(categoryOptional.get());
                    }
                    else {
                        Category category = new Category();
                        category.setTitle(nextLine[0]);
                        category = categoryRepository.save(category);
                        product.setCategory(category);
                    }
                }
                if(!nextLine[2].isEmpty() && product.getPrice()!=Double.parseDouble(nextLine[2])){
                    product.setPrice(Double.parseDouble(nextLine[2]));
                }
                if(!nextLine[3].isEmpty() && !product.getTitle().equals(nextLine[3])){
                    product.setTitle(nextLine[3]);
                }
                if(!nextLine[4].isEmpty() && !product.getShortDescription().equals(nextLine[4])){
                    product.setShortDescription(nextLine[4]);
                }
                if(!nextLine[5].isEmpty() && !product.getFullDescription().equals(nextLine[5])){
                    product.setFullDescription(nextLine[5]);
                }
                products.add(product);
            }
            productRepository.saveAll(products);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public List<Product> getAllProducts() {
        return (List<Product>)(productRepository.findAll());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Page<Product> getAllProductsByPage(int pageNumber, int pageSize) {
        return productRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public boolean isProductWithTitleExists(String productTitle) {
        return productRepository.findOneByTitle(productTitle) != null;
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
    public String removeProduct(Product product){
        if (productRepository.getOrgerCountOfProduct(product.getId())<1){
            productRepository.delete(product);
            imageSaverService.removeImages(product.getImages());
            return "Продукт успешно удалён";
        }
        else {
            return ("Нельзя удалить продукт, который уже был заказан");
        }
    }
}
