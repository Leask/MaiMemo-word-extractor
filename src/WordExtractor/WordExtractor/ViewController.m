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

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //    SubString *subStr = [SubString new];
    //    [subStr updateWithString:@"hello world" start:0 end:6];
    //    SubString *subStr1 = [SubString new];
    //    [subStr1 updateWithString:@"hello johy" start:0 end:6];
    //    NSMutableSet *set = [NSMutableSet new];
    //    [set addObject:subStr];
    //    if ([set containsObject:subStr1]) {
    //        NSLog(@"%@", @"contains");
    //    } else {
    //        NSLog(@"%@, %lu, %lu", @"not contains", subStr.hash, subStr1.hash);
    //    }
    NSString *s = [NSString stringWithContentsOfFile:@"/Users/TJT/all_vocs.txt" encoding:NSUTF8StringEncoding error:nil];
    
    //    NSArray *arr = [@"pick ... up\npick apples\npick at\npick fruit\npick on\npick on sb.\npick out\npick sb. out\npick sb. up\npick sth. out\npick sth. up\npick up\npick up leaves" componentsSeparatedByString:@"\n"];
    PhraseSearchTree *tree = [[PhraseSearchTree alloc] initWithLibrary:[s componentsSeparatedByString:@"\n"]];
    [tree build];
    NSLog(@"%@", [tree search:@"pick sth. up"]);
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
    // Dispose of any resources that can be recreated.
}

@end
