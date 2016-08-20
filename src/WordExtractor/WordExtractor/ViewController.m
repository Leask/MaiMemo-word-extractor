//
//  ViewController.m
//  WordExtractor
//
//  Created by TJT on 8/17/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "ViewController.h"
#import "SubString.h"
#import "TextIterators.h"
#import "PhraseSearchTree.h"
#import "StringWrapper.h"
#import "WordExtractor.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSString *s = [NSString stringWithContentsOfFile:@"/Users/TJT/all_vocs.txt" encoding:NSUTF8StringEncoding error:nil];
    NSString *text = [NSString stringWithContentsOfFile:@"/Users/TJT/alice30.txt" encoding:NSUTF8StringEncoding error:nil];
    NSArray<NSString *> *library = [s componentsSeparatedByString:@"\n"];
    WordExtractor *extractor = [[WordExtractor alloc] initWithLibrary:library];
    [extractor buildIndex];
    NSArray *arr = [extractor extract:text];
    for (Result *r in arr) {
        NSLog(@"%@", r.word);
    }
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
    // Dispose of any resources that can be recreated.
}

@end
