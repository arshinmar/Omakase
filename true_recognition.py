import io
import os
import time as time

# Imports the Google Cloud client library
from google.cloud import vision
from google.cloud.vision import types
import cv2

client = vision.ImageAnnotatorClient()

def get_image_info(image_path):
    client = vision.ImageAnnotatorClient()
    file_name = os.path.abspath(image_path)

    # Loads the image into memory
    with io.open(file_name, 'rb') as image_file:
        content = image_file.read()

    image = types.Image(content=content)
    return image

def get_object_info(image,suppress=True):
    client = vision.ImageAnnotatorClient()
    # Performs object detection on the image file
    response = client.object_localization(image=image,max_results=20)

    objects = response.localized_object_annotations
    if suppress!=True:
        print('Number of objects found: {}'.format(len(objects)))
        for object_ in objects:
            print('\n{} (confidence: {})'.format(object_.name, object_.score))
    return objects

def get_text_info(image,suppress=True):
    client = vision.ImageAnnotatorClient()
    response = client.text_detection(image=image)
    texts = response.text_annotations

    if suppress!=True:
        print('Texts:')
        for text in texts:
            print('\n"{}"'.format(text.description))

            vertices = (['({},{})'.format(vertex.x, vertex.y)
                        for vertex in text.bounding_poly.vertices])

            print('bounds: {}'.format(','.join(vertices)))
    return texts

def get_label_info(image,suppress=True):
    client = vision.ImageAnnotatorClient()
    response = client.label_detection(image=image)
    labels = response.label_annotations

    if suppress!=True:
        print('Labels:')
        for label in labels:
            print(label.description)
    return labels

def check_overlap(y_max1,y_min2):
    if (y_max1<=y_min2):
        return False
    else:
        return True

def bounders(height,width,objects):
    bounding_box_list=[]

    for object_ in objects:
        x_max,y_max,x_min,y_min=-1,-1,-2,-2
        for vertex in object_.bounding_poly.normalized_vertices:
            if x_max==-1:
                x_max=vertex.x
            else:
                if vertex.x>x_max:
                    x_min=x_max
                    x_max=vertex.x
                else:
                    x_min=vertex.x
            if y_max==-1:
                y_max=vertex.y
            else:
                if vertex.y<y_max:
                    y_min=y_max
                    y_max=vertex.y
                else:
                    y_min=vertex.y
        entry=(int(x_min*width),int(y_max*height),int(x_max*width),int(y_min*height))
        bounding_box_list+=[entry]

    bounding_box_list= sorted(bounding_box_list, key=lambda tup: tup[1])
    return bounding_box_list

def true_vals(bounding_box_list,height):
    shelf_indexes=[]
    i=0
    shift=1
    last_one=False
    while i<len(bounding_box_list)-1:
        if (i+shift==len(bounding_box_list)-1):
            last_one=True
        try:
            if check_overlap(bounding_box_list[i][3],bounding_box_list[i+shift][1])==False and last_one!=True:
                shelf_indexes+=[(bounding_box_list[i][1],bounding_box_list[i+shift-1][3])]
                i+=shift
                shift=1
            else:
                if last_one==True:
                    shelf_indexes+=[(bounding_box_list[i][1],bounding_box_list[i+shift-1][3])]
                shift+=1
        except:
            break
    true_indexes=[]
    min_val=shelf_indexes[0][0]
    min_val_max=shelf_indexes[0][1]
    for i in range(0,len(shelf_indexes),1):
        if i==len(shelf_indexes)-1:
            if check_overlap(min_val_max,shelf_indexes[i][0])==True:
                true_indexes+=[(min_val,shelf_indexes[i][1])]
            else:
                true_indexes+=[(shelf_indexes[i][0],shelf_indexes[i][1])]
        else:
            if check_overlap(min_val_max,shelf_indexes[i+1][0])==False:
                true_indexes+=[(min_val,shelf_indexes[i+1][0])]
                min_val=shelf_indexes[i+1][0]
                min_val_max=shelf_indexes[i+1][1]
    true_indexes=[(22,true_indexes[0][0])]+true_indexes+[(true_indexes[-1][1],height-22)]
            #if shelf_indexes[i][1]<shelf_indexes[i+1][0]:
            #    true_indexes+=[(shelf_indexes[i][0],shelf_indexes[i+1][0])]
    return true_indexes

def draw_rectangles(image_path,objects):
    #output_directory='one_level_down/'
    start=time.time()
    img=cv2.imread(image_path)
    height, width, shape = img.shape
    bounding_box_list=bounders(height,width,objects)
    true_indexes=true_vals(bounding_box_list,height)
    print(true_indexes, 'TRUE VALS')
    counter=0
    for i in bounding_box_list:
        img = cv2.rectangle(img,(i[0],i[1]),(i[2],i[3]),(0,255,0),8)
        cv2.putText(img, str(counter), (i[0], i[1]-10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0,255,0), 3)
        counter+=1

    img_true=cv2.imread(image_path)
    all_image_paths=[]
    for i in range(0,len(true_indexes),1):
        img = cv2.rectangle(img,(25,true_indexes[i][0]),(width-25,true_indexes[i][1]),(255,255,0),3)
        path = 'output_'+str(i)+'.png'
        all_image_paths+=[path]
        cv2.imwrite(path,img_true[true_indexes[i][0]-15:true_indexes[i][1],0:width])

    cv2.imwrite('true_output.png',img)
    end=time.time()
    print(end-start,'TIME ELAPSED')
    return all_image_paths

def redraw(all_image_paths):
    #output_directory='two_levels_down/'

    all_bounding_box_info=[]
    all_new_image_paths=[]
    for image_path in all_image_paths:
        image=get_image_info(image_path)
        objects=get_object_info(image)
        img=cv2.imread(image_path)
        height, width, shape = img.shape
        bounding_box_list=bounders(height,width,objects)
        for i in bounding_box_list:
            img = cv2.rectangle(img,(i[0],i[1]),(i[2],i[3]),(0,255,0),5)
        new_image_path=image_path[:len(image_path)-4]+'_changes.png'
        cv2.imwrite(new_image_path,img)
        all_new_image_paths+=[new_image_path]
        all_bounding_box_info+=[bounding_box_list]
    return all_new_image_paths, all_bounding_box_info

def return_all_object_label_info(all_new_image_paths):
    full_list=[]

    redundant_stuff=['Food','Fruit','Plant', 'Vegetable', 'Plastic', 'Glass Bottle', 'Dairy', 'Water', 'Drink','Dessert', 'Cup', 'Toy']
    for new_image_path in all_new_image_paths:
        temp_entry=[]
        image=get_image_info(new_image_path)
        labels=get_label_info(image,suppress=True)
        objects=get_object_info(image,suppress=True)
        for label in labels:
            if label.description not in redundant_stuff:
                temp_entry+=[label.description]
        for object_ in objects:
            if object_.name not in redundant_stuff:
                temp_entry+=[object_.name]
        single_entry=[temp_entry]
        text_entry=[]
        if 'Packaged goods' in temp_entry:
            texts=get_text_info(image,suppress=True)
            flip=False
            for text in texts:
                if flip==True:
                    text_entry+=[text.description]
                flip=True
        single_entry+=[text_entry]
        full_list+=[single_entry]

    return full_list

def create_specific_images(all_new_image_paths,all_bounding_box_info):
    output_directory='output_files'
    full_image_paths=[]
    increment=20
    for i in range(0,len(all_new_image_paths),1):
        for j in range(0,len(all_bounding_box_info[i]),1):
            new_image=cv2.imread(all_new_image_paths[i])
            new_writing_path=output_directory+'/'+all_new_image_paths[i][:len(all_new_image_paths[i])-4]+'_'+str(j)+'.png'
            temp_entry=all_bounding_box_info[i][j]
            try:
                cv2.imwrite(new_writing_path,new_image[temp_entry[1]-increment:temp_entry[3]+increment,temp_entry[0]-increment:temp_entry[2]+increment])
                full_image_paths+=[new_writing_path]
            except:
                continue
    return full_image_paths

def main_pipeline(image_path):
    client = vision.ImageAnnotatorClient()
    start=time.time()
    # Instantiates a client
    image=get_image_info(image_path)
    objects=get_object_info(image)
    end=time.time()
    print(end-start, 'TIME ELAPSED FOR OBJECT DETECTION')

    all_image_paths=draw_rectangles(image_path,objects)
    #print(all_image_paths)
    all_new_image_paths,all_bounding_box_info=redraw(all_image_paths)
    #print(all_new_image_paths)
    full_image_paths=create_specific_images(all_new_image_paths,all_bounding_box_info)
    #print(full_image_paths)
    full_list=return_all_object_label_info(full_image_paths)
    end=time.time()
    print(end-start,'TIME ELAPSED FOR COMPLETION')
    return full_list

image_path='images/20200823_093157-min.jpg'
full_list=main_pipeline(image_path)
