package jbr.aws.s3ops.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * Product model class.
 * 
 * @author Ranjith Sekar 
 * @since 2021-Jun-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductModel {
    private Long id;
    private String name;
    private String category;
    private String price;
}
