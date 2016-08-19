//
//  PhraseSearchTree.h
//  WordExtractor
//
//  Created by TJT on 8/19/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PhraseSearchTree : NSObject

@property (atomic, assign) BOOL         isBuilded;

- (instancetype)initWithLibrary:(NSArray<NSString *> *)library;

- (void)build;

- (NSArray *)search:(NSString *)sentence;

@end
